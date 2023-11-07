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
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.Filter.equalTo
import com.google.firebase.firestore.Filter.or
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
    private var locationSelected = false
    private val filterLocations = mutableListOf<String>()
    private lateinit var view : View
    private lateinit var dogAdapter : DogAdapter
    private lateinit var dogBreedFilterAdapter: DogFilterAdapter
    private lateinit var dogLocationFilterAdapter: DogFilterAdapter
    private lateinit var adapterAutocomplete: ArrayAdapter<String>
    private lateinit var breeds: MutableList<String>
    private lateinit var subBreeds: MutableList<String>
    private val filterMap = mutableMapOf<String, MutableList<Filter>>()
    private var orderByDate : Boolean = false

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

    override fun onPause() {
        super.onPause()
        filterMap.clear()
        dogAdapter.stopListening()
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
        binding.recyclerFilter.visibility = View.GONE

        setupRecyclerView()
        setupSwipeRefreshSettings()

        loadBreedsAndSubBreeds{
            setupRecyclerFilterLocation()
            setupAutocompleteTextView()
            setupRecyclerFilterBreed()
        }

        binding.AutoCompleteTextViewBreedSearch.text.clear()

        binding.buttonSearchByBreed.setOnClickListener {
            searchByBreedFilter()
        }

        binding.textHomeMoreFilters.setOnClickListener{
            searchByOtherFilters()
        }
    }

    private fun setupAutocompleteTextView(){
        val combinedList = mutableListOf<String>()
        combinedList.addAll(breeds)
        combinedList.addAll(subBreeds)

        adapterAutocomplete = ArrayAdapter<String>(requireContext(), android.R.layout.simple_dropdown_item_1line, combinedList)
        binding.AutoCompleteTextViewBreedSearch.setAdapter(adapterAutocomplete)
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
        dogAdapter.startListening()
        binding.recyclerHomeDogs.adapter = dogAdapter
    }

    private fun applyFilters(){
        var query = db.collection("dogs")
            .whereEqualTo("adopterName", "")

        for (filter in filterMap.values.flatten()){
            query = query.where(filter)
        }

        val queryDirection =
            if (orderByDate) Query.Direction.ASCENDING
            else Query.Direction.DESCENDING


        query = query.orderBy("publicationDate", queryDirection)
        val config = PagingConfig(20,10,  false)

        val options = FirestorePagingOptions.Builder<DogEntity>()
            .setLifecycleOwner(this)
            .setQuery(query, config, DogEntity::class.java)
            .build()

        dogAdapter.updateOptions(options)
        binding.recyclerHomeDogs.adapter = dogAdapter
        dogAdapter.refresh()
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

    private fun setupRecyclerFilterBreed() {
        setupBreedFilter{
            dogBreedFilterAdapter = DogFilterAdapter(breeds, this)
            setupRecyclerViewSettings(binding.recyclerFilter,true)
            binding.recyclerFilter.adapter = dogBreedFilterAdapter
            binding.recyclerFilter.visibility = View.VISIBLE
        }
    }

    private fun setupRecyclerViewSettings(recycler : RecyclerView, isHorizontal : Boolean = false) {
        recycler.setHasFixedSize(true)
        val linearLayoutManager = if (isHorizontal) LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false) else LinearLayoutManager(context)
        recycler.layoutManager = linearLayoutManager
    }

    private fun setupRecyclerFilterLocation() {
        setupLocationFilter{
            dogLocationFilterAdapter = DogFilterAdapter(filterLocations, this)
            setupRecyclerViewSettings(binding.recyclerFilter,true)
            binding.recyclerFilter.adapter = dogLocationFilterAdapter
        }
    }

    private fun setupBreedFilter(action: () -> Unit){
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val result = fetchBreedDogs().sorted()

                breeds.clear()
                breeds.addAll(result)
                action()
            } catch (e: Exception) {
                Toast.makeText(view.context, resources.getString(R.string.homeFilterLoadBreedsError), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupLocationFilter(action: () -> Unit){
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val result = fetchLocationsDogs().sorted()
                filterLocations.clear()
                filterLocations.addAll(result)
                action()
            } catch (e: Exception) {
                Toast.makeText(view.context, resources.getString(R.string.homeFilterLoadLocationsError), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun getRetrofit(): Retrofit{
        return Retrofit.Builder()
            .baseUrl("https://dog.ceo/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun loadBreedsAndSubBreeds(action: () -> Unit){
        CoroutineScope(Dispatchers.IO).launch {
            val call = getRetrofit().create(ApiService::class.java).getDogsAllBreed("api/breeds/list/all")
            if (!call.isSuccessful) return@launch

            val dogs = call.body()
            val map = dogs?.dogBreeds ?: emptyMap()
            breeds = map.keys.toMutableList()
            subBreeds = map.values.flatten().toSet().toMutableList()

            safeActivityCall {
                requireActivity().runOnUiThread{
                    safeAccessBinding{
                        action()
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
                        val breed = document.getString("breed")?: continue

                        if(breeds.contains(breed) && !dogBreedsAux.contains(breed)){
                            dogBreedsAux.add(breed)
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
                        val location = document.getString("location")?: continue

                        if(!locationsList.contains(location)){
                            locationsList.add(location)
                        }
                    }
                    cont.resume(locationsList)
                }
        }
    }

    private fun searchByOtherFilters(){
        val popupMenu = PopupMenu(context, binding.textHomeMoreFilters)
        popupMenu.menuInflater.inflate(R.menu.filters_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.getByBreed -> {
                    binding.recyclerFilter.adapter = dogBreedFilterAdapter
                    locationSelected = false
                    return@setOnMenuItemClickListener true
                }
                R.id.getByDate -> {
                    searchByDate()
                    return@setOnMenuItemClickListener true
                }
                R.id.getByLocation -> {
                    binding.recyclerFilter.adapter = dogLocationFilterAdapter
                    locationSelected = true
                    return@setOnMenuItemClickListener true
                }
                R.id.clearFilters -> {
                    filterMap.clear()
                    dogBreedFilterAdapter.clearFilter()
                    dogLocationFilterAdapter.clearFilter()
                    binding.AutoCompleteTextViewBreedSearch.text.clear()
                    orderByDate = false
                    applyFilters()
                    return@setOnMenuItemClickListener true
                }
                else -> return@setOnMenuItemClickListener false
            }
        }
        popupMenu.show()
    }

    private fun searchByBreedFilter(){
        val searchBreed = binding.AutoCompleteTextViewBreedSearch.text.toString()

        if (searchBreed.isEmpty()){
            if (filterMap.containsKey("searchBreed")) {
                filterMap["searchBreed"]?.clear()
            }
        } else{
            if (!breeds.contains(searchBreed) && !subBreeds.contains(searchBreed)){
                binding.AutoCompleteTextViewBreedSearch.text.clear()
                Toast.makeText(context, resources.getString(R.string.homeFilterBreedNotFound), Toast.LENGTH_SHORT).show()
                return
            }

            if (!filterMap.containsKey("searchBreed")) {
                filterMap["searchBreed"] = mutableListOf()
            }
            filterMap["searchBreed"]?.clear()
            filterMap["searchBreed"]?.add(or(equalTo("breed", searchBreed), equalTo("subBreed", searchBreed)))
        }

        applyFilters()
    }

    override fun onFilterItemSelected(itemList: MutableList<String>) {
        val valueToChange = if (locationSelected) "location" else "breed"

        if (itemList.size > 0) {
            if (!filterMap.containsKey(valueToChange)) {
                filterMap[valueToChange] = mutableListOf()
            }
            val filter = Filter.inArray(valueToChange, itemList)
            filterMap[valueToChange]?.add(filter)
        } else {
            if (filterMap.containsKey(valueToChange)) {
                filterMap[valueToChange]?.clear()
            }
        }
        applyFilters()
    }

    private fun searchByDate(){
        orderByDate = !orderByDate
        applyFilters()
    }

    private fun safeActivityCall(action: () -> Unit) {
        try{
            if (!requireActivity().isFinishing && !requireActivity().isDestroyed ) {
                action()
            }
        } catch (_: IllegalStateException) {
            // El activity ya no existia
        }
    }

    private fun safeAccessBinding(action: () -> Unit) {
        if (_binding != null && context != null) {
            action()
        }
    }
}