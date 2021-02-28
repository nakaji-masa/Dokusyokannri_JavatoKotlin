package android.wings.websarva.dokusyokannrijavatokotlin.notification.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.detail.activities.DetailActivity
import android.wings.websarva.dokusyokannrijavatokotlin.detail.fragments.DetailSelectFragment
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.FireStoreHelper
import android.wings.websarva.dokusyokannrijavatokotlin.notification.Notification
import android.wings.websarva.dokusyokannrijavatokotlin.notification.NotificationHelper
import android.wings.websarva.dokusyokannrijavatokotlin.notification.NotificationsAdapter
import android.wings.websarva.dokusyokannrijavatokotlin.utils.DividerHelper
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.fragment_notification.*

class NotificationFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notification, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // リサイクラービューの設定
        notificationRecyclerView.addItemDecoration(DividerHelper.createDivider(requireContext()))
        notificationRecyclerView.setHasFixedSize(true)
        notificationRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = NotificationsAdapter(requireContext(), NotificationHelper.getNotificationList())
        notificationRecyclerView.adapter = adapter

        // リスナー設定
        adapter.setOnNotificationClickListener(object :
            NotificationsAdapter.OnNotificationClickListener {
            override fun onNotificationClickListener(notification: Notification) {
                if (notification.type == NotificationHelper.TYPE_LIKE) {
                    FireStoreHelper.updateLikedNotification(notification)
                } else {
                    FireStoreHelper.updateCommentedNotification(notification)
                }

                // バッチの更新
                val nav: BottomNavigationView =
                    requireActivity().findViewById(R.id.bottomNavigationView)
                nav.menu.getItem(3).itemId.let {
                    nav.getOrCreateBadge(it).apply {
                        number = NotificationHelper.getNotCheckedNotificationCount()
                        if (number == 0) {
                            isVisible = false
                        }
                    }
                }

                // 画面遷移
                val intent = Intent(requireActivity(), DetailActivity::class.java)
                intent.putExtra(DetailActivity.BOOK_ID, notification.bookHelper.docId)
                intent.putExtra(DetailSelectFragment.TAB_POST_START, true)
                requireActivity().startActivity(intent)
            }
        })
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            NotificationFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

}


