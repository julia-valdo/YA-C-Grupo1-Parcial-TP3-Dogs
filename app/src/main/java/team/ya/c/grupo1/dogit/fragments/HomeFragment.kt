package team.ya.c.grupo1.dogit.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingConfig
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import team.ya.c.grupo1.dogit.R
import team.ya.c.grupo1.dogit.adapters.DogAdapter
import team.ya.c.grupo1.dogit.adapters.DogBreedAdapter
import team.ya.c.grupo1.dogit.databinding.FragmentHomeBinding
import team.ya.c.grupo1.dogit.entities.DogEntity
import team.ya.c.grupo1.dogit.listeners.OnViewItemClickedListener
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import team.ya.c.grupo1.dogit.apiInterface.ApiService
import team.ya.c.grupo1.dogit.listeners.OnFilterItemClickedListener
import java.io.StringReader


class HomeFragment : Fragment(), OnViewItemClickedListener, OnFilterItemClickedListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()
    var BASE_URL: String = "https://dog.ceo/api/breeds/list/all"
    private val dogBreeds = mutableListOf<String>()
    private val allDogBreeds = mutableListOf<String>()
    private lateinit var view : View
    private lateinit var dogAdapter : DogAdapter
    private lateinit var  dogBreedAdapter: DogBreedAdapter
    private  lateinit var adapterAutocomplete: ArrayAdapter<String>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate( inflater, container, false)
        view = binding.root

        return view
    }

    override fun onStart() {
        super.onStart()
        setupVariables()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewItemDetail(item: Any) {
        val dog = if (item is DogEntity) item else return

        val action = HomeFragmentDirections.actionHomeFragmentToDetailsFragment(dog)
        this.findNavController().navigate(action)
    }

    private fun setupVariables() {
        binding.progressBarHome.visibility = View.VISIBLE
        setupRecyclerView()
        setupRecyclerFilterBreed()
        setupSwipeRefreshSettings()
        binding.buttonSearchByBreed.setOnClickListener {
            searchByBreedFilter()
        }
    }

    private fun setupRecyclerView() {
        val query = db.collection("dogs")
            .whereEqualTo("adopterName", "")

        val config = PagingConfig(20,10,  false)

        val options = FirestorePagingOptions.Builder<DogEntity>()
            .setLifecycleOwner(this)
            .setQuery(query, config, DogEntity::class.java)
            .build()

        setupRecyclerViewSettings(binding.recyclerHomeDogs)
        dogAdapter = DogAdapter(options, this)

        setupLoadStateSettings()

        binding.recyclerHomeDogs.adapter = dogAdapter
    }
    private fun setupLoadStateSettings() {
        viewLifecycleOwner.lifecycleScope.launch {
            dogAdapter.loadStateFlow.collectLatest { loadStates ->
                when(loadStates.refresh){
                    is LoadState.Loading -> {
                        binding.progressBarHome.visibility = View.VISIBLE
                    }
                    is LoadState.NotLoading -> {
                        binding.progressBarHome.visibility = View.GONE
                    }
                    is LoadState.Error -> {
                        Toast.makeText(context, resources.getString(R.string.home_loading_dogs_failed), Toast.LENGTH_SHORT).show()
                    }
                }

                when(loadStates.append){
                    is LoadState.Loading -> {
                    }
                    is LoadState.NotLoading -> {
                    }
                    is LoadState.Error -> {
                        Toast.makeText(context, resources.getString(R.string.home_loading_dogs_failed), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun setupSwipeRefreshSettings() {
        binding.swipeRefreshHome.setOnRefreshListener {
            dogAdapter.refresh()
            binding.swipeRefreshHome.isRefreshing = false
        }
    }

 private fun getRetrofit(): Retrofit{

      return Retrofit.Builder()
         .baseUrl("https://dog.ceo/")
         .addConverterFactory(GsonConverterFactory.create())
         .build()
 }
    private fun setupRecyclerFilterBreed() {
        dogBreedAdapter = DogBreedAdapter(dogBreeds, this)
        setupRecyclerViewSettings(binding.recyclerFilterBreed,true)
        binding.recyclerFilterBreed.adapter = dogBreedAdapter
        searchAllBreed()
    }
    private fun searchAllBreed(){
        CoroutineScope(Dispatchers.IO).launch {
            val call = getRetrofit().create(ApiService::class.java).getDogsAllBreed("api/breeds/list")
            val dogs = call.body()
            safeActivityCall {
                requireActivity().runOnUiThread{
                    if(call.isSuccessful){
                        val breeds = dogs?.dogBreeds ?: emptyList()
                        val allDogBreeds = dogs?.dogBreeds ?: emptyList()
                        adapterAutocomplete = ArrayAdapter<String>(requireContext(),android.R.layout.simple_dropdown_item_1line,allDogBreeds)
                        binding.AutoCompleteTextViewBreedSearch.setAdapter(adapterAutocomplete)
                        dogBreeds.clear()
                        dogBreeds.addAll(breeds)
                        dogBreedAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }
    override fun onFilterItemSelected(item: MutableList<String>) {
        if (item.size == 0) return
        dogAdapter.stopListening()
        val query = db.collection("dogs")
            .whereIn("race",item)

        val config = PagingConfig(20,10,  false)

        val options = FirestorePagingOptions.Builder<DogEntity>()
            .setLifecycleOwner(this)
            .setQuery(query, config, DogEntity::class.java)
            .build()

        dogAdapter.updateOptions(options)
        dogAdapter.startListening()
        binding.recyclerHomeDogs.adapter = dogAdapter
    }
    private fun searchByBreedFilter(){
        dogAdapter.stopListening()
        val searchBreed = binding.AutoCompleteTextViewBreedSearch.text.toString()

        val query = db.collection("dogs")
            .whereEqualTo("race",searchBreed)

        val config = PagingConfig(20,10,  false)

        val options = FirestorePagingOptions.Builder<DogEntity>()
            .setLifecycleOwner(this)
            .setQuery(query, config, DogEntity::class.java)
            .build()

        dogAdapter.updateOptions(options)
        dogAdapter.startListening()
        binding.recyclerHomeDogs.adapter = dogAdapter
    }
  private fun setupRecyclerViewSettings(recycler : RecyclerView, isHorizontal : Boolean = false) {
      recycler.setHasFixedSize(true)
      val linearLayoutManager = if (isHorizontal) LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false) else LinearLayoutManager(context)
      recycler.layoutManager = linearLayoutManager
  }
    private fun safeActivityCall(action: () -> Unit) {
        if (!requireActivity().isFinishing && !requireActivity().isDestroyed) {
            action()
        }
    }
}