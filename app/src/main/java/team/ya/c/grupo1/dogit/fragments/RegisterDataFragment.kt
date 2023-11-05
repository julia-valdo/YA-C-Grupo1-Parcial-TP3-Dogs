package team.ya.c.grupo1.dogit.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import team.ya.c.grupo1.dogit.R
import team.ya.c.grupo1.dogit.databinding.FragmentRegisterDataBinding
import team.ya.c.grupo1.dogit.entities.UserEntity

class RegisterDataFragment : Fragment() {

    private lateinit var v : View

    private lateinit var btnRegister : Button

    private var _binding : FragmentRegisterDataBinding? = null
    private val binding get() = _binding!!

    private lateinit var user : UserEntity

    private lateinit var mAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterDataBinding.inflate(inflater, container, false)
        v = binding.root
        return v
    }

    override fun onStart() {
        super.onStart()

        val email = RegisterDataFragmentArgs.fromBundle(requireArguments()).email
        val password = RegisterDataFragmentArgs.fromBundle(requireArguments()).password

        btnRegister = binding.btnRegisterDataRegister
        btnRegister.setOnClickListener {
            initializeUser(email)
            if(isValidUser()) {
                register(email, password)
            }
        }
    }

    private fun initializeUser(email: String) {
        user = UserEntity(
            binding.txtRegisterDataName.text.toString(),
            binding.txtRegisterDataSurname.text.toString(),
            email,
            "",
            binding.txtRegisterDataPhone.text.toString(),
            ""
        )
    }

    private fun isValidUser(): Boolean {
        val propertiesToCheck = listOf(
            Pair(user.firstName, resources.getString(R.string.registerNameError)),
            Pair(user.surname, resources.getString(R.string.registerSurnameError)),
            Pair(user.telephoneNumber, resources.getString(R.string.registerPhoneError))
        )

        for ((property, errorMessage) in propertiesToCheck) {
            if (property.isNullOrEmpty() || property.isBlank()) {
                Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT).show()
                return false
            }
        }

        if (user.telephoneNumber.length != 10) {
            Toast.makeText(activity, resources.getString(R.string.registerPhoneFormatError), Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun register(email: String, password: String) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val dbRegister = FirebaseFirestore.getInstance()
                    user.uuid = FirebaseAuth.getInstance().currentUser?.uid!!
                    dbRegister.collection("users").document(email).set(
                        hashMapOf(
                            "firstName" to user.firstName,
                            "surname" to user.surname,
                            "email" to email,
                            "telephoneNumber" to user.telephoneNumber,
                            "uuid" to user.uuid
                        ))
                    findNavController().navigate(R.id.action_registerDataFragment_to_loginFragment)
                }
                else Toast.makeText(activity, resources.getString(R.string.registerGenericError), Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}