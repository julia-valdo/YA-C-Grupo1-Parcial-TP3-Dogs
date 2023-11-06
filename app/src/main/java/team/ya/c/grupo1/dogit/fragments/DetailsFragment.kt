package team.ya.c.grupo1.dogit.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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
    private lateinit var adoptBtn : Button
    private var ownerName : String? = null
    private var ownerPhone : String? = null
    private var profileImage : String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        view = binding.root

        getOwnerData()


        return view
    }
    override fun onStart() {
        super.onStart()
        val activity = activity as MainActivity
        activity.hideBottomNavMenu()

        this.view.findViewById<ImageView>(R.id.btnBottomSheetDetailsFollow).setOnClickListener {
            addToFavorites()
        }
    }

    private fun addToFavorites() {
        val dog = DetailsFragmentArgs.fromBundle(requireArguments()).dog
        val userEmail = FirebaseAuth.getInstance().currentUser?.email?: return

        val imgFavorite = this.view.findViewById<ImageView>(R.id.btnBottomSheetDetailsFollow)
        imgFavorite.setOnClickListener {
            if (dog.followers.contains(userEmail)) {
                Toast.makeText(view.context, "Se ha eliminado de favoritos", Toast.LENGTH_SHORT).show()
                imgFavorite.setImageDrawable(ContextCompat.getDrawable(view.context, R.drawable.icon_follow))
                dog.followers.remove(userEmail)
            } else {
                Toast.makeText(view.context, "Se ha añadido a favoritos", Toast.LENGTH_SHORT).show()
                imgFavorite.setImageDrawable(ContextCompat.getDrawable(view.context, R.drawable.icon_follow_filled))
                dog.followers.add(userEmail!!)
            }
            updateDogFollowers(dog.followers, dog.id)
        }
    }

    private fun updateDogFollowers(followers: MutableList<String>, idDog: String) {
        FirebaseFirestore.getInstance()
            .collection("dogs")
            .document(idDog)
            .update("followers", followers)
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
        binding.DetailsBottomSheet.infoBoxDetailsBottomSheetWeight.txtInfoBoxContent.text = DetailsFragmentArgs.fromBundle(requireArguments()).dog.weight.toString() + resources.getString(R.string.detailsWeightSign)
        binding.DetailsBottomSheet.infoBoxDetailsBottomSheetWeight.txtInfoBoxLabel.text = resources.getString(R.string.detailsWeight)
        binding.DetailsBottomSheet.infoBoxDetailsBottomSheetGender.txtInfoBoxContent.text = DetailsFragmentArgs.fromBundle(requireArguments()).dog.gender
        binding.DetailsBottomSheet.infoBoxDetailsBottomSheetGender.txtInfoBoxLabel.text = resources.getString(R.string.detailsGender)
        binding.DetailsBottomSheet.txtDetailsBottomSheetOwner.text = ownerName
        setFavorite(DetailsFragmentArgs.fromBundle(requireArguments()).dog.followers)

        loadImages()

        adoptBtn = view.findViewById(R.id.btnBottomSheetDetailsAdopt)

        if (DetailsFragmentArgs.fromBundle(requireArguments()).dog.adopterEmail != "") {
            adoptBtn.visibility = View.GONE
        }
    }

    private fun setFavorite(followers: MutableList<String>) {
        val userEmail = FirebaseAuth.getInstance().currentUser?.email?:return

        val imgFavorite = this.view.findViewById<ImageView>(R.id.btnBottomSheetDetailsFollow)
        if(followers.contains(userEmail))
            imgFavorite.setImageDrawable(ContextCompat.getDrawable(view.context, R.drawable.icon_follow_filled))
        else
            imgFavorite.setImageDrawable(ContextCompat.getDrawable(view.context, R.drawable.icon_follow))
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
    private fun loadImages(){
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

        try{
            Glide.with(view.context)
                .load(profileImage)
                .placeholder(R.drawable.img_avatar)
                .error(R.drawable.img_avatar)
                .into(binding.DetailsBottomSheet.imgBottomSheetDetailsProfilePicture)
        } catch (e: Exception){
            binding.DetailsBottomSheet.imgBottomSheetDetailsProfilePicture.setImageResource(R.drawable.img_avatar)
        }

    }
    private fun safeActivityCall(action: () -> Unit) {
        val activity = requireActivity() as MainActivity

        if (!activity.isFinishing && !activity.isDestroyed) {
            action()
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

        binding.DetailsBottomSheet.btnBottomSheetDetailsCall.setOnClickListener {
            callOwnerPhone()
        }

        adoptBtn.setOnClickListener {
            adoptDog()
        }
    }

    private fun callOwnerPhone(){
        startActivity( Uri.parse("tel:${ownerPhone}").let { number ->
            Intent(Intent.ACTION_DIAL, number)
        })
    }

    private fun getOwnerData(){
        try{
            FirebaseFirestore.getInstance()
                .collection("users")
                .document(DetailsFragmentArgs.fromBundle(requireArguments()).dog.ownerEmail)
                .get()
                .addOnSuccessListener {
                    safeActivityCall {
                        ownerName = it.getString("firstName") + " " + it.getString("surname")
                        ownerPhone = it.getString("telephoneNumber")
                        profileImage = it.getString("profileImage")
                        replaceData()
                        setUpButtons()
                        prepareBottomSheet()
                    }
                }.addOnFailureListener{
                    profileImage = ""
                    ownerName = ""
                    ownerPhone = ""
                    replaceData()
                    setUpButtons()
                    prepareBottomSheet()
                }
        } catch (e: Exception){
            profileImage = ""
            ownerName = ""
            ownerPhone = ""
            replaceData()
            setUpButtons()
            prepareBottomSheet()
        }
    }
    private fun adoptDog(){
        val dog = DetailsFragmentArgs.fromBundle(requireArguments()).dog
        val userEmail = FirebaseAuth.getInstance().currentUser?.email?: return

        val db = FirebaseFirestore.getInstance()
        val query = db.collection("users").document(userEmail)

        query.get()
            .addOnSuccessListener {
                val documentUser = it.data ?: return@addOnSuccessListener

                val name = documentUser["firstName"].toString() + " " + documentUser["surname"].toString()
                dog.adopterName = name
                db.collection("dogs").document(dog.id).update("adopterName", dog.adopterName)
                db.collection("dogs").document(dog.id).update("adopterEmail", userEmail)

                val text = resources.getString(R.string.detailsDogAdopted).replace("{dogName}", dog.name)
                Toast.makeText(activity, text, Toast.LENGTH_SHORT).show()

                adoptBtn.visibility = View.GONE
            }
            .addOnFailureListener {
                Toast.makeText(activity, resources.getString(R.string.detailsDogAdoptedFailed), Toast.LENGTH_SHORT).show()
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