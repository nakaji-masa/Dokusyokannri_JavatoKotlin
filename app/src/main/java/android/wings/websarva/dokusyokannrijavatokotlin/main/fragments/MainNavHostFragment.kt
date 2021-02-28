package android.wings.websarva.dokusyokannrijavatokotlin.main.fragments

import android.wings.websarva.dokusyokannrijavatokotlin.main.navigator.MainNavigator
import androidx.navigation.Navigator
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.NavHostFragment

class MainNavHostFragment : NavHostFragment() {
    override fun createFragmentNavigator(): Navigator<out FragmentNavigator.Destination> {
        return MainNavigator(requireContext(), childFragmentManager, id)
    }
}