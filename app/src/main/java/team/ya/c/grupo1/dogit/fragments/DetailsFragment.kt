package team.ya.c.grupo1.dogit.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import team.ya.c.grupo1.dogit.R
import team.ya.c.grupo1.dogit.activities.MainActivity
import team.ya.c.grupo1.dogit.databinding.FragmentDetailsBinding

class DetailsFragment : Fragment() {

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var view : View
    private lateinit var bottomSheetBehavior : BottomSheetBehavior<View>
    private var imgIndex : Int = 0
    private var cantImg : Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        view = binding.root

        replaceData()
        setUpButtons()
        prepareBottomSheet()

        return view
    }
    override fun onStart() {
        super.onStart()
        val activity = activity as MainActivity
        activity.hideBottomNavMenu()
    }

    override fun onStop() {
        super.onStop()
        val activity = activity as MainActivity
        activity.showBottomNavMenu()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun replaceData() {
        binding.DetailsBottomSheet.txtDetailsBottomSheetName.text = DetailsFragmentArgs.fromBundle(requireArguments()).dog.name
        binding.DetailsBottomSheet.txtDetailsBottomSheetAge.text = DetailsFragmentArgs.fromBundle(requireArguments()).dog.age.toString()
        binding.DetailsBottomSheet.txtDetailsBottomSheetDescription.text = DetailsFragmentArgs.fromBundle(requireArguments()).dog.description
        binding.DetailsBottomSheet.txtDetailsBottomSheetLocation.text = DetailsFragmentArgs.fromBundle(requireArguments()).dog.location
        binding.DetailsBottomSheet.infoBoxDetailsBottomSheetWeight.txtInfoBoxContent.text = DetailsFragmentArgs.fromBundle(requireArguments()).dog.weight.toString()
        binding.DetailsBottomSheet.infoBoxDetailsBottomSheetGender.txtInfoBoxContent.text = DetailsFragmentArgs.fromBundle(requireArguments()).dog.gender

        loadImage()
    }

    private fun prepareBottomSheet(){
        val bs = view.findViewById<View>(R.id.DetailsBottomSheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bs)

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        binding.DetailsBottomSheet.imgBottomSheetDetailsHorizontalLine.setOnClickListener {
            if(bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }else{
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback(){
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
            override fun onStateChanged(bottomSheet: View, newState: Int) {
            }
        })
    }
    private fun loadImage(){
        try{
            Glide.with(view.context)
                .load(DetailsFragmentArgs.fromBundle(requireArguments()).dog.images[0])
                .placeholder(R.drawable.img_default_dog)
                .error(R.drawable.img_default_dog)
                .into(binding.imgDetailsDog1)
        } catch (e: Exception){
            binding.imgDetailsDog1.setImageResource(R.drawable.img_default_dog)
        }
        finally {
            cantImg = DetailsFragmentArgs.fromBundle(requireArguments()).dog.images.size
            binding.imgDetailsDog1.setOnClickListener{
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }
    }

    private fun setUpButtons() {
        binding.btnDetailsNext.setOnClickListener{
            if(cantImg > 1){
                imgIndex++
                if(imgIndex == cantImg){
                    imgIndex = 0
                }
                changeImage()
            }
        }
        binding.btnDetailsPrevious.setOnClickListener{
            if(cantImg > 1){
                imgIndex--
                if(imgIndex == -1){
                    imgIndex = cantImg - 1
                }
                changeImage()
            }
        }
    }
    private fun changeImage(){
        try{
            Glide.with(view.context)
                .load(DetailsFragmentArgs.fromBundle(requireArguments()).dog.images[imgIndex])
                .placeholder(R.drawable.img_default_dog)
                .error(R.drawable.img_default_dog)
                .into(binding.imgDetailsDog1)
        } catch (e: Exception){
            binding.imgDetailsDog1.setImageResource(R.drawable.img_default_dog)
        }
    }
}