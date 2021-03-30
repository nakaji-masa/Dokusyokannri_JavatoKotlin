package android.wings.websarva.dokusyokannrijavatokotlin.main.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.databinding.FragmentMainSelectBinding
import android.wings.websarva.dokusyokannrijavatokotlin.notification.NotificationHelper
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainSelectFragment : Fragment() {

    private var _binding: FragmentMainSelectBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainSelectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        GlobalScope.launch(Dispatchers.Main) {

            // notificationListを作成
            NotificationHelper.createNotificationList()

            binding.bottomNavigationView.menu.getItem(3).itemId.let {
                binding.bottomNavigationView.getOrCreateBadge(it).apply {
                    number = NotificationHelper.getNotCheckedNotificationCount()
                        if (number == 0) {
                            isVisible = false
                        }
                    }
                }
        }
        binding.bottomNavigationView.setupWithNavController(requireActivity().findNavController(R.id.navFragment))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



    companion object {
        @JvmStatic
        fun newInstance() =
            MainSelectFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}