package android.wings.websarva.dokusyokannrijavatokotlin.main.fragments

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.notification.NotificationHelper
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import kotlinx.android.synthetic.main.fragment_main_select.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainSelectFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main_select, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val handler = Handler()
        GlobalScope.launch {

            // notificationListを作成
            NotificationHelper.createNotificationList()

            handler.post {
                bottomNavigationView.menu.getItem(3).itemId.let {
                    bottomNavigationView.getOrCreateBadge(it).apply {
                        number = NotificationHelper.getNotCheckedNotificationCount()
                        if (number == 0) {
                            isVisible = false
                        }
                    }
                }
            }
        }
        bottomNavigationView.setupWithNavController(requireActivity().findNavController(R.id.navFragment))
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