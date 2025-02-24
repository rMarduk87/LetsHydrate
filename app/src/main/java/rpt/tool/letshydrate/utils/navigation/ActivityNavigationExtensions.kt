package rpt.tool.letshydrate.utils.navigation

import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import rpt.tool.letshydrate.R
import rpt.tool.letshydrate.utils.log.w

val FragmentActivity.safeNavController: NavController?
    get() {
        runCatching {
            findNavControllerWithFragmentManager()
        }.onSuccess {
            return it
        }

        runCatching {
            findNavControllerWithNavHostId()
        }.onSuccess {
            return it
        }.onFailure {
            w(it)
        }

        return null
    }

private fun FragmentActivity.findNavControllerWithFragmentManager(): NavController? {
    runCatching {
        (supportFragmentManager.findFragmentById(R.id.init_user_info_activity_nav_host_fragment) as NavHostFragment)
            .navController
    }.onSuccess {
        return it
    }

    return null
}

private fun FragmentActivity.findNavControllerWithNavHostId(): NavController? {
    runCatching {
        findNavController(R.id.init_user_info_activity_nav_host_fragment)
    }.onSuccess {
        return it
    }

    return null
}