package team.ya.c.grupo1.dogit.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import team.ya.c.grupo1.dogit.R
import team.ya.c.grupo1.dogit.activities.MainActivity
import team.ya.c.grupo1.dogit.databinding.FragmentLoginBinding
import team.ya.c.grupo1.dogit.databinding.FragmentPreloginBinding

class PreLoginFragment : Fragment() {

    private lateinit var v : View

    private var _binding : FragmentPreloginBinding? = null

    private val binding get() = _binding!!

    private lateinit var preLoginButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
        ): View? {
            _binding = FragmentPreloginBinding.inflate(inflater, container, false)
            v = binding.root

            return v
    }

    override fun onStart() {
        super.onStart()

        checkUser()

        preLoginButton = binding.btnPreloginMain
        preLoginButton.setOnClickListener {
            findNavController().navigate(R.id.action_preloginFragment_to_loginFragment)
        }
    }

    private fun checkUser() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if(currentUser != null && currentUser.isEmailVerified) {
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}