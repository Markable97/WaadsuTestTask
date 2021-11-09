package com.glushko.waadsutesttask.presentation_layer.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.glushko.waadsutesttask.business_logic_layer.domain.Marker
import com.glushko.waadsutesttask.business_logic_layer.interactor.UseCaseRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import org.json.JSONObject

class MapViewModel(application: Application) : AndroidViewModel(application) {

    private var myCompositeDisposable: CompositeDisposable? = null
    private val useCase = UseCaseRepository()
    private val _liveDataMarker: MutableLiveData<List<Marker>> = MutableLiveData()
    val liveDataMarker = _liveDataMarker
    val liveDataAPI: MutableLiveData<Boolean> = MutableLiveData()
    private val _liveDataCoordinate: MutableLiveData<JSONObject> = MutableLiveData()
    val liveDataCoordinate: LiveData<JSONObject> = _liveDataCoordinate


    init {
        myCompositeDisposable = CompositeDisposable()
    }

    fun getCoordinates() {
        //Получаем данные с сервера
        myCompositeDisposable?.addAll(
            useCase.getCoordinates()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handlerResponse, this::handleError)
        )
    }

    fun getMarkers(obj: JSONObject) {
        //Получения маркеров - точек с центром и длиной
        myCompositeDisposable?.addAll(
            useCase.getMarkers(
                obj.getJSONArray("features").getJSONObject(0).getJSONObject("geometry")
                    .getJSONArray("coordinates"))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ markers ->
                    _liveDataMarker.postValue(markers)
                    /*markers.forEach {
                        _liveDataMarker.postValue(it)
                    }*/
                }, { println("Ошибка на маркерах") })
        )
    }

    private fun handleError(err: Throwable) {
        println("MapViewModel err: ${err.localizedMessage}")
        liveDataAPI.postValue(false)
    }
    private fun handlerResponse(response: ResponseBody) {
        //Данные с сервера
        val obj = JSONObject(response.string())
        //Скажем что данные пришли, скроем progressBar
        liveDataAPI.postValue(true)
        //Объект для отрисовки
        _liveDataCoordinate.postValue(obj)
    }

    override fun onCleared() {
        super.onCleared()
        myCompositeDisposable?.clear()
    }
}