package com.example.gregorio.capstone.dagger

import android.app.Application
import com.example.gregorio.capstone.ui.FavouritesFragment
import com.example.gregorio.capstone.ui.MapFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, MapFragmentModule::class, FavouriteFragmentModule::class])
interface AppComponent {
    fun inject(app: Application)
    fun inject(mapFragment: MapFragment): MapFragment?
    fun inject(favouriteFragment: FavouritesFragment): FavouritesFragment?
}
