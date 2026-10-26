package com.build.gcunlock.hooks.general

import com.build.gcunlock.core.AppRule
import com.build.gcunlock.core.ConfigSnapshot

object HookPolicy {

    data class Row(
        val pkg: String,
        val fps: Boolean = false,
        val display: Boolean = false,
        val frameProfile: Boolean = false,
        val sr: Boolean = false,
        val thermal: Boolean = false,
        val monster: Boolean = false,
    )

    fun resolve(pkg: String, config: ConfigSnapshot): Row {
        val rule: AppRule? = config.perAppRules[pkg]
        return Row(
            pkg          = pkg,
            fps          = config.fpsOverride > 0 || (rule?.gameFps ?: 0) > 0,
            display      = config.hold144HzIdle,
            frameProfile = rule != null,
            sr           = config.srEnabled || rule?.srCustom == true,
            thermal      = config.thermalBypass || rule?.thermalBypass == true,
            monster      = config.monsterPlusEnabled || (rule?.monsterPlus ?: 0) > 0,
        )
    }
}
