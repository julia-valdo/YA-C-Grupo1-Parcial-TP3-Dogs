package team.ya.c.grupo1.dogit.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.PopupMenu
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
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import team.ya.c.grupo1.dogit.R
import team.ya.c.grupo1.dogit.adapters.DogAdapter
import team.ya.c.grupo1.dogit.adapters.DogFilterAdapter
import team.ya.c.grupo1.dogit.databinding.FragmentHomeBinding
import team.ya.c.grupo1.dogit.entities.DogEntity
import team.ya.c.grupo1.dogit.listeners.OnViewItemClickedListener
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import team.ya.c.grupo1.dogit.apiInterface.ApiService
import team.ya.c.grupo1.dogit.listeners.OnFilterItemClickedListener
import kotlin.coroutines.resume



class HomeFragment : Fragment(), OnViewItemClickedListener, OnFilterItemClickedListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()
    private val dogBreeds = mutableListOf<String>()
    private val filterLocations = mutableListOf<String>()
    private val allDogBreeds = mutableListOf<String>()
    private lateinit var view : View
    private lateinit var dogAdapter : DogAdapter
    private lateinit var  dogFilterAdapter: DogFilterAdapter
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
        binding.progressBarHomeBottom.visibility = View.GONE
        setupRecyclerView()
        setupRecyclerFilterBreed()
        setupSwipeRefreshSettings()
        binding.buttonSearchByBreed.setOnClickListener {
            searchByBreedFilter()
        }
        binding.textHomeMoreFilters.setOnClickListener{
            searchByOtherFilters()
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
                        Toast.makeText(context, resources.getString(R.string.homeLoadingDogsFailed), Toast.LENGTH_SHORT).show()
                        binding.progressBarHome.visibility = View.GONE
                    }
                }

                when(loadStates.append){
                    is LoadState.Loading -> {
                        binding.progressBarHomeBottom.visibility = View.VISIBLE
                    }
                    is LoadState.NotLoading -> {
                        binding.progressBarHomeBottom.visibility = View.GONE
                    }
                    is LoadState.Error -> {
                        Toast.makeText(context, resources.getString(R.string.homeLoadingDogsFailed), Toast.LENGTH_SHORT).show()
                        binding.progressBarHomeBottom.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun setupSwipeRefreshSettings() {
        safeAccessBinding {
            binding.swipeRefreshHome.setOnRefreshListener {
                dogAdapter.refresh()
                binding.swipeRefreshHome.isRefreshing = false
            }
        }
    }

    private fun getRetrofit(): Retrofit{

      return Retrofit.Builder()
         .baseUrl("https://dog.ceo/")
         .addConverterFactory(GsonConverterFactory.create())
         .build()
 }
    private fun setupRecyclerFilterBreed() {
        dogFilterAdapter = DogFilterAdapter(dogBreeds, this)
        setupRecyclerViewSettings(binding.recyclerFilter,true)
        binding.recyclerFilter.adapter = dogFilterAdapter
        searchAllBreed()
    }
    private fun setupRecyclerFilterLocation() {
        dogFilterAdapter = DogFilterAdapter(filterLocations, this)
        binding.recyclerFilter.adapter = dogFilterAdapter
        setupLocationFilter()
    }
    private fun setupBreedFilter(){
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val result = fetchBreedDogs()
                dogBreeds.clear()
                dogBreeds.addAll(result)
                dogFilterAdapter.notifyDataSetChanged()
            } catch (e: Exception) {

            }
        }
    }
    private fun setupLocationFilter(){
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val result = fetchLocationsDogs()
                filterLocations.clear()
                filterLocations.addAll(result)
                dogFilterAdapter.notifyDataSetChanged()
            } catch (e: Exception) {

            }
        }
    }
    private fun setupRecyclerViewSettings(recycler : RecyclerView, isHorizontal : Boolean = false) {
        recycler.setHasFixedSize(true)
        val linearLayoutManager = if (isHorizontal) LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false) else LinearLayoutManager(context)
        recycler.layoutManager = linearLayoutManager
    }
    private fun searchAllBreed(){
        val combinedList = mutableListOf<String>()
        CoroutineScope(Dispatchers.IO).launch {
            val call = getRetrofit().create(ApiService::class.java).getDogsAllBreed("api/breeds/list/all")
            val dogs = call.body()
            safeActivityCall {
                requireActivity().runOnUiThread{
                    safeAccessBinding{
                        if(call.isSuccessful){
                            val breeds = dogs?.dogBreeds ?: emptyMap()
                            for ((breed, subcategories) in breeds) {
                                combinedList.add(breed)
                                allDogBreeds.add(breed)
                                if (subcategories.isNotEmpty()) {
                                    for (subBreeds in subcategories){
                                        if(!combinedList.contains(subBreeds)){
                                            combinedList.add(subBreeds)
                                        }
                                    }
                                }
                            }
                            adapterAutocomplete = ArrayAdapter<String>(requireContext(),android.R.layout.simple_dropdown_item_1line,combinedList)
                            binding.AutoCompleteTextViewBreedSearch.setAdapter(adapterAutocomplete)
                            dogBreeds.clear()
                            dogBreeds.addAll(allDogBreeds)
                            setupBreedFilter()
                        }
                    }
                }
            }
        }

    }
    private suspend fun fetchBreedDogs(): List<String>{

        return suspendCancellableCoroutine {
            cont ->
            db.collection("dogs")
                .whereEqualTo("adopterName", "")
                .get()
                .addOnSuccessListener { documents ->
                    val dogBreedsAux = mutableListOf<String>()
                    for (document in documents) {
                        val race = document.getString("race").toString()
                        if (race != null) {
                            if(dogBreeds.contains(race) && !dogBreedsAux.contains(race)){
                                dogBreedsAux.add(race)
                            }
                        }
                    }
                    cont.resume(dogBreedsAux)
                }
        }
    }
    private suspend fun fetchLocationsDogs(): List<String>{

        return suspendCancellableCoroutine {
                cont ->
            db.collection("dogs")
                .whereEqualTo("adopterName", "")
                .get()
                .addOnSuccessListener { documents ->
                    val locationsList = mutableListOf<String>()
                    for (document in documents) {
                        val location = document.getString("location").toString()
                        if (location != null) {
                            if(!locationsList.contains(location)){
                                locationsList.add(location)
                            }
                        }
                    }
                    cont.resume(locationsList)
                }
        }
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

    private fun searchByOtherFilters(){
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.menuInflater.inflate(R.menu.filters_menu, popupMenu.menu)


        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.getByBreed -> {
                    setupRecyclerFilterBreed()
                    return@setOnMenuItemClickListener true
                }
                R.id.getByDate -> {
                    searchByDate()
                    return@setOnMenuItemClickListener true
                }
                R.id.getByLocation -> {
                    setupRecyclerFilterLocation()
                    return@setOnMenuItemClickListener true
                }

                else -> return@setOnMenuItemClickListener false
            }
        }

        popupMenu.show()
    }
    private fun searchByDate(){
        dogAdapter.stopListening()

        val query = db.collection("dogs")
            .orderBy("publicationDate", Query.Direction.DESCENDING)

        val config = PagingConfig(20,10,  false)

        val options = FirestorePagingOptions.Builder<DogEntity>()
            .setLifecycleOwner(this)
            .setQuery(query, config, DogEntity::class.java)
            .build()

        dogAdapter.updateOptions(options)
        dogAdapter.startListening()
        binding.recyclerHomeDogs.adapter = dogAdapter
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
    private fun safeActivityCall(action: () -> Unit) {
        if (!requireActivity().isFinishing && !requireActivity().isDestroyed ) {
            action()
        }
    }
    private fun safeAccessBinding(action: () -> Unit) {
        if (_binding != null && context != null) {
            action()
        }
    }
}