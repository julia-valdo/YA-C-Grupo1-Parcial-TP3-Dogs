package team.ya.c.grupo1.dogit.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingConfig
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import team.ya.c.grupo1.dogit.R
import team.ya.c.grupo1.dogit.adapters.DogAdapter
import team.ya.c.grupo1.dogit.databinding.FragmentAdoptionBinding
import team.ya.c.grupo1.dogit.entities.DogEntity
import team.ya.c.grupo1.dogit.listeners.OnViewItemClickedListener

class AdoptionFragment : Fragment(), OnViewItemClickedListener {

    private var _binding: FragmentAdoptionBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()

    private lateinit var view : View
    private lateinit var dogAdapter : DogAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAdoptionBinding.inflate(inflater, container, false)
        view = binding.root

        return binding.root
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

        val action = AdoptionFragmentDirections.actionAdoptionFragmentToDetailsFragment(dog)
        this.findNavController().navigate(action)
    }

    private fun setupVariables() {
        binding.progressBarAdoption.visibility = View.VISIBLE
        binding.progressBarAdoptionBottom.visibility = View.GONE
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser.let { user ->
            if (user != null) {
                db.collection("users")
                    .document(user.email!!)
                    .get()
                    .addOnSuccessListener { document ->
                        if (document.contains("adoptedDogs")){
                            val adoptedDogs = document.get("adoptedDogs") as List<*>

                            if (adoptedDogs.isEmpty()) {
                                binding.progressBarAdoption.visibility = View.GONE
                                Toast.makeText(context, resources.getString(R.string.adoptionListIsEmpty), Toast.LENGTH_SHORT).show()
                                return@addOnSuccessListener
                            }

                            setupRecyclerView(adoptedDogs.filterIsInstance<String>())
                        } else {
                            Toast.makeText(context, resources.getString(R.string.adoptionLoadingDogsFailed), Toast.LENGTH_SHORT).show()
                            binding.progressBarAdoption.visibility = View.GONE
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, resources.getString(R.string.adoptionLoadingDogsFailed), Toast.LENGTH_SHORT).show()
                        binding.progressBarAdoption.visibility = View.GONE
                    }
            } else{
                Toast.makeText(context, resources.getString(R.string.adoptionLoadingDogsFailed), Toast.LENGTH_SHORT).show()
                binding.progressBarAdoption.visibility = View.GONE
            }
        }
    }

    private fun setupRecyclerView(adoptedDogs: List<String>) {
        val query = db.collection("dogs")
            .whereIn("id", adoptedDogs)

        val config = PagingConfig(20, 10, false)

        val options = FirestorePagingOptions.Builder<DogEntity>()
            .setLifecycleOwner(this)
            .setQuery(query, config, DogEntity::class.java)
            .build()

        setupRecyclerViewSettings(binding.recyclerAdoption)

        dogAdapter = DogAdapter(options, this)
        setupLoadStateSettings()

        binding.recyclerAdoption.adapter = dogAdapter
    }

    private fun setupRecyclerViewSettings(recycler : RecyclerView) {
        recycler.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(context)
        recycler.layoutManager = linearLayoutManager
    }

    private fun setupLoadStateSettings() {
        viewLifecycleOwner.lifecycleScope.launch {
            dogAdapter.loadStateFlow.collectLatest { loadStates ->
                when(loadStates.refresh){
                    is LoadState.Loading -> {
                        binding.progressBarAdoption.visibility = View.VISIBLE
                    }
                    is LoadState.NotLoading -> {
                        binding.progressBarAdoption.visibility = View.GONE
                    }
                    is LoadState.Error -> {
                        Toast.makeText(context, resources.getString(R.string.adoptionLoadingDogsFailed), Toast.LENGTH_SHORT).show()
                        binding.progressBarAdoption.visibility = View.GONE
                    }
                }

                when(loadStates.append){
                    is LoadState.Loading -> {
                        binding.progressBarAdoptionBottom.visibility = View.VISIBLE
                    }
                    is LoadState.NotLoading -> {
                        binding.progressBarAdoptionBottom.visibility = View.GONE
                    }
                    is LoadState.Error -> {
                        Toast.makeText(context, resources.getString(R.string.homeLoadingDogsFailed), Toast.LENGTH_SHORT).show()
                        binding.progressBarAdoptionBottom.visibility = View.GONE
                    }
                }
            }
        }
    }

}