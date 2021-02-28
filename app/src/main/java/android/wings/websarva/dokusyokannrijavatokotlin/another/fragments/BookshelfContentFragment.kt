package android.wings.websarva.dokusyokannrijavatokotlin.another.fragments

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.another.AnotherUserContentBookShelfAdapter
import android.wings.websarva.dokusyokannrijavatokotlin.another.activities.AnotherUserActivity
import android.wings.websarva.dokusyokannrijavatokotlin.another.fragments.base.BaseAnotherUserFragment
import android.wings.websarva.dokusyokannrijavatokotlin.bookshelf.fragments.GridItemDecoration
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.FireStoreHelper
import android.wings.websarva.dokusyokannrijavatokotlin.firebase.model.UserInfoHelper
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_another_user_bookshelf_content.*


class BookshelfContentFragment : BaseAnotherUserFragment() {

    private lateinit var adapterAnotherUser: AnotherUserContentBookShelfAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_another_user_bookshelf_content, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val dm = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(dm)
        val width = dm.widthPixels / requireContext().resources.displayMetrics.density.toInt()

        contentBookShelfRecyclerView.layoutManager = GridLayoutManager(
            requireContext(),
            3,
            GridLayoutManager.VERTICAL,
            false
        )

        contentBookShelfRecyclerView.addItemDecoration(GridItemDecoration(width, 3))
        contentBookShelfRecyclerView.setHasFixedSize(true)

        val userJson = activity?.intent?.extras?.getString(AnotherUserActivity.ANOTHER_USER_KEY)
        val userInfo = Gson().fromJson<UserInfoHelper>(userJson, UserInfoHelper::class.java)
        adapterAnotherUser = AnotherUserContentBookShelfAdapter(FireStoreHelper.getRecyclerOptionsFromUid(userInfo.uid))
        contentBookShelfRecyclerView.adapter = adapterAnotherUser
    }

    override fun onStart() {
        super.onStart()
        adapterAnotherUser.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapterAnotherUser.stopListening()
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            BookshelfContentFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}