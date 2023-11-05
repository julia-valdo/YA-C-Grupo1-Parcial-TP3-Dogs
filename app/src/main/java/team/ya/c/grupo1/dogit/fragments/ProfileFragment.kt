package team.ya.c.grupo1.dogit.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import team.ya.c.grupo1.dogit.databinding.FragmentProfileBinding
import com.google.firebase.firestore.FirebaseFirestore
import team.ya.c.grupo1.dogit.R
import team.ya.c.grupo1.dogit.activities.MainActivity

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var view : View
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        view = binding.root

        return view
    }

    override fun onStart() {
        super.onStart()

        val activity = activity as MainActivity
        activity.hideBottomNavMenu()

        binding.profileBtnAddPhoto.setOnClickListener {
            binding.editTxtPublicationDogName.visibility = View.VISIBLE
            binding.profileBtnSavePhoto.visibility = View.VISIBLE
            binding.profileBtnAddPhoto.visibility = View.GONE
        }

        binding.profileBtnSavePhoto.setOnClickListener {
            val urlPhoto = binding.editTxtPublicationDogName.text.toString()

            if (urlPhoto.isEmpty() || urlPhoto.isBlank()){
                Toast.makeText(view.context, resources.getString(R.string.profileCompleteEditTextError), Toast.LENGTH_LONG).show()
            } else {
                updatePhoto(urlPhoto)
            }
        }

        replaceData()
    }

    private fun updatePhoto(urlPhoto: String) {
        val userEmail = FirebaseAuth.getInstance().currentUser?.email ?: return

        val userRef = FirebaseFirestore.getInstance().collection("users").document(userEmail)
        val data = hashMapOf<String, Any>(
            "profileImage" to urlPhoto
        )

        userRef.update(data)
            .addOnSuccessListener {
                binding.editTxtPublicationDogName.visibility = View.GONE
                binding.profileBtnSavePhoto.visibility = View.GONE
                binding.profileBtnAddPhoto.visibility = View.VISIBLE
                replaceData()
                Toast.makeText(view.context, resources.getString(R.string.profileUploadImageSuccess), Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener {
                Toast.makeText(view.context, resources.getString(R.string.profileUploadImageError), Toast.LENGTH_LONG).show()
            }
    }

    override fun onStop() {
        super.onStop()

        val activity = activity as MainActivity
        activity.showBottomNavMenu()
    }

    private fun replaceData() {
        val userEmail = FirebaseAuth.getInstance().currentUser?.email?:return

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(userEmail)
            .get()
            .addOnSuccessListener {
                safeActivityCall {
                    binding.profileTxtName.text = it.getString("firstName")
                    val profileImage = it.getString("profileImage")

                    if (profileImage == "") {
                        binding.profileImg.setImageResource(R.drawable.img_avatar)
                    } else {
                        Glide.with(view.context)
                            .load(profileImage)
                            .placeholder(R.drawable.img_avatar)
                            .error(R.drawable.img_avatar)
                            .into(binding.profileImg)
                    }

                }
            }
    }

    private fun safeActivityCall(action: () -> Unit) {
       val activity = requireActivity() as MainActivity

        if (!activity.isFinishing && !activity.isDestroyed) {
            action()
        }
    }
}