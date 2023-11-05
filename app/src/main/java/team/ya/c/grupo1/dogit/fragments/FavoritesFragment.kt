package team.ya.c.grupo1.dogit.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingConfig
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import team.ya.c.grupo1.dogit.R
import team.ya.c.grupo1.dogit.adapters.DogAdapter
import team.ya.c.grupo1.dogit.databinding.FragmentFavoritesBinding
import team.ya.c.grupo1.dogit.entities.DogEntity
import team.ya.c.grupo1.dogit.listeners.OnViewItemClickedListener

class FavoritesFragment : Fragment(), OnViewItemClickedListener {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private val db = FirebaseFirestore.getInstance()

    private lateinit var view : View
    private lateinit var dogAdapter : DogAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        view = binding.root

        return view
    }

    override fun onStart() {
        super.onStart()
        setupRecyclerView()
        setupSwipeRefreshSettings()
    }

    private fun setupRecyclerView() {
        val userEmail = FirebaseAuth.getInstance().currentUser?.email?:return

        val query = db.collection("dogs")
            .whereArrayContains("followers", userEmail)

        val config = PagingConfig(20, 10, false)

        val options = FirestorePagingOptions.Builder<DogEntity>()
            .setLifecycleOwner(this)
            .setQuery(query, config, DogEntity::class.java)
            .build()

        setupRecyclerViewSettings(binding.recycleViewFavorites)

        dogAdapter = DogAdapter(options, this)
        binding.recycleViewFavorites.adapter = dogAdapter
    }

    private fun setupRecyclerViewSettings(recyclerView: RecyclerView) {
        recyclerView.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(context)
        binding.recycleViewFavorites.layoutManager = linearLayoutManager
    }

    private fun setupSwipeRefreshSettings() {
        binding.swipeRefreshFavorites.setOnRefreshListener {
            dogAdapter.refresh()
            binding.swipeRefreshFavorites.isRefreshing = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewItemDetail(item: Any) {
        val dog = if (item is DogEntity) item else return

        val action = FavoritesFragmentDirections.actionFavoritesFragmentToDetailsFragment(dog)
        this.findNavController().navigate(action)
    }
}