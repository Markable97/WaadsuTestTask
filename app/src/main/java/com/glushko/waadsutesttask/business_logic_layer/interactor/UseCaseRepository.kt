package com.glushko.waadsutesttask.business_logic_layer.interactor


import com.glushko.waadsutesttask.business_logic_layer.domain.Marker
import com.glushko.waadsutesttask.data_layer.datasource.NetworkService
import com.google.android.gms.maps.model.LatLng
import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.ResponseBody
import org.json.JSONArray
import kotlin.math.*


class UseCaseRepository() {

    fun getCoordinates(): Observable<ResponseBody>{
        return NetworkService.makeNetworkServiceRxJava().getCoordinates()
    }

    fun getMarkers(coordinates: JSONArray): Single<List<Marker>>{
        val markers: MutableList<Marker> = mutableListOf()
        //Идем по массиву полигонов
        for(num in 0 until coordinates.length() ){
            var length: Double = 0.0
            var sumX: Double = 0.0
            var sumY: Double = 0.0
            //вытаксктваем полигон
            val polygon = coordinates.getJSONArray(num).getJSONArray(0)
            println("Polygon[$num] = $polygon")
            for(numPoint in 0 until polygon.length() - 1){
                //Вытаскиваем точку
                var pointObj = polygon.getJSONArray(numPoint)
                //Считаем сумму по х и у для центра
                sumX += pointObj.get(0) as Double
                sumY += pointObj.get(1) as Double
                val pointOne: Pair<Double, Double> = Pair(pointObj.get(0) as Double, pointObj.get(1) as Double)
                //И след за ней точку, чтобы посчитать расстояние
                pointObj = polygon.getJSONArray(numPoint + 1)
                val pointTwo: Pair<Double, Double> = Pair(pointObj.get(0) as Double, pointObj.get(1) as Double)
                //Считает расстояние между двуся точками
                length+=getLengthSegment(pointOne, pointTwo)

            }
            //Находим центр полигона
            val len = polygon.length() - 1 //Последний не берем, так как замкнутый
            val lon = sumX/len //долгота
            val lat = sumY/len //широта
            val centerPolygon = LatLng(lat,lon)
            //Запоминаем
            markers.add(Marker("Длина участка = ${length.roundToInt()}", centerPolygon))
        }

        return Single.just(markers)
    }

    // This function converts decimal degrees to radians
    private fun deg2rad(deg: Double): Double {
        return deg * Math.PI / 180
    }

    private fun getLengthSegment(
        pointOne: Pair<Double, Double>,
        pointTwo: Pair<Double, Double>
    ): Double {
        val earthRadiusKm = 6371.009
        val (x1, y1) = pointOne
        val (x2, y2) = pointTwo
        //Переводим в радианы
        val lon1r: Double = deg2rad(x1)
        val lat1r: Double = deg2rad(y1)
        val lon2r: Double = deg2rad(x2)
        val lat2r: Double = deg2rad(y2)
        //Формула "гаверсинуса"
        val u: Double = sin((lat2r - lat1r) / 2)
        val v: Double = sin((lon2r - lon1r) / 2)
        return 2.0 * earthRadiusKm * asin(sqrt(u * u + cos(lat1r) * cos(lat2r) * v * v))
    }

}