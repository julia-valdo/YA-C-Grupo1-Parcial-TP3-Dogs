package team.ya.c.grupo1.dogit.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import team.ya.c.grupo1.dogit.R
import team.ya.c.grupo1.dogit.adapters.DogAdapter
import team.ya.c.grupo1.dogit.databinding.FragmentFavoritesBinding
import team.ya.c.grupo1.dogit.entities.DogEntity

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private lateinit var view : View
    private lateinit var rvDog: RecyclerView

    //TODO: Integrar con firebase
    private val dogs: MutableList<DogEntity> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        view = binding.root
        rvDog = binding.recycleViewFavorites

        val linearLayoutManager = LinearLayoutManager(context)

        //rvDog.adapter = DogAdapter(dogs, requireContext())
        rvDog.layoutManager = linearLayoutManager

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}