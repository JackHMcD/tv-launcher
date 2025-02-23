package nl.ndat.tvlauncher.data.resolver

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import nl.ndat.tvlauncher.data.entity.App

class AppResolver {
	companion object {
		private val launcherCategories = arrayOf(
			Intent.CATEGORY_LEANBACK_LAUNCHER,
			Intent.CATEGORY_LAUNCHER,
		)

		const val APP_ID_PREFIX = "app:"
	}

	fun getApplication(context: Context, packageId: String): App? {
		val packageManager = context.packageManager

		return launcherCategories
			.map { category ->
				val intent = Intent(Intent.ACTION_MAIN, null)
					.addCategory(category)
					.setPackage(packageId)
				packageManager.queryIntentActivities(intent, 0)
			}
			.flatten()
			.distinctBy { it.activityInfo.name }
			.map { it.toApp(packageManager) }
			.firstOrNull()
	}

	fun getApplications(context: Context): List<App> {
		val packageManager = context.packageManager

		return launcherCategories
			.map { category ->
				val intent = Intent(Intent.ACTION_MAIN, null).addCategory(category)
				packageManager.queryIntentActivities(intent, 0)
			}
			.flatten()
			.distinctBy { it.activityInfo.name }
			.map { it.toApp(packageManager) }
	}

	private fun ResolveInfo.toApp(packageManager: PackageManager) = App(
		id = "$APP_ID_PREFIX${activityInfo.name}",

		displayName = activityInfo.loadLabel(packageManager).toString(),
		packageName = activityInfo.packageName,

		launchIntentUriDefault = packageManager.getLaunchIntentForPackage(activityInfo.packageName)?.toUri(0),
		launchIntentUriLeanback = packageManager.getLeanbackLaunchIntentForPackage(activityInfo.packageName)?.toUri(0),
	)
}
