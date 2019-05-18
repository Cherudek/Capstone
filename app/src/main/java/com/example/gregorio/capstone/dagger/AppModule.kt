package com.example.gregorio.capstone.dagger

import android.app.Application
import android.content.Context
import com.example.gregorio.capstone.ui.FavouritesFragment
import com.example.gregorio.capstone.ui.MapFragment
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(private val app: Application) {
    @Provides
    @Singleton
    fun provideContext(): Context = app
}

@Module
class MapFragmentModule(private val mapFragment: MapFragment) {
    @Provides
    @Singleton
    fun provideMapFragment(): MapFragment = mapFragment
}

@Module
class FavouriteFragmentModule(private val favouriteFragment: FavouritesFragment) {
    @Provides
    @Singleton
    fun provideFavouriteFragment(): FavouritesFragment = favouriteFragment
}