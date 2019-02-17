package com.github.rougsig.filetemplateloader

import timber.log.Timber

class WhenSelect : Function1<Route, Key> {
  fun getKey(route: Route): Key {
    return route.getKey()
  }

  override fun invoke(route: Route): Key {
    return when (route) {
      is Route.Key1 -> ScreenKey1()
      is Route.Key2 -> ScreenKey2()
      is Route.Key3 -> ScreenKey3()
    }
  }
}
