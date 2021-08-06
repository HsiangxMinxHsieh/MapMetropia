package com.timmy.mapmetropia.model

import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName


data class JourneyData(
    @SerializedName("ended_on")
    val endedOn: Int = 0, //Arrival time of this route
    @SerializedName("estimated_time")
    val estimatedTime: Int = 0, // The fare status,（1=All segments have tickets, 2=Partial tickets, 3=No tickets)
    @SerializedName("fare_status")
    val fareStatus: Int = 0, // The fare status,（1=All segments have tickets, 2=Partial tickets, 3=No tickets)
    @SerializedName("mode")
    val mode: String = "",
    @SerializedName("started_on")
    val startedOn: Int = 0,  // Departure time of this route
    @SerializedName("steps")
    val steps: List<Step> = listOf(),
    @SerializedName("total_price")
    val totalPrice: Double = 0.0, //Suggested total price for this route
    @SerializedName("trip_detail_uuid")
    val tripDetailUuid: String = "" // The trip detail uuid
) {
    data class Step(
        @SerializedName("arrive")
        val arrive: Int = 0, // How long will the next bus come
        @SerializedName("delay")
        val delay: Int = 0, // Delay time of the ride (unit: minute)
        @SerializedName("destination_lat")
        val destinationLat: Double = 0.0, // The parking lot other information
        @SerializedName("destination_lng")
        val destinationLng: Double = 0.0, // The parking lot other information
        @SerializedName("destination_name")
        val destinationName: String = "", // The destination name
        @SerializedName("destination_no")
        val destinationNo: String = "", // The destination station number
        @SerializedName("distance")
        val distance: Int = 0, // The steps distance (unit: meter)
        @SerializedName("ended_on")
        val endedOn: Int = 0, // Arrival time of the ride
        @SerializedName("estimated_time")
        val estimatedTime: Int = 0, //Estimated time of this steps (unit: second)
        @SerializedName("firststop")
        val firststop: Boolean = false, // If true is equal to starting station, it must show arrive
        @SerializedName("is_puyuma")
        val isPuyuma: Boolean = false,
        @SerializedName("is_ticket")
        val isTicket: Int = 0,
        @SerializedName("mode")
        val mode: String = "", //The steps traval mode (options: "train", "tram", "bus", "driving", "cycling", "walking", "subway")
        @SerializedName("mode_type")
        val modeType: String = "",
        @SerializedName("num_stops")
        val numStops: Int = 0, // Number of stations
        @SerializedName("origin_lat")
        val originLat: Double = 0.0, // The parking lot other information
        @SerializedName("origin_lng")
        val originLng: Double = 0.0, // The parking lot other information
        @SerializedName("origin_name")
        val originName: String = "", // The origin name
        @SerializedName("origin_no")
        val originNo: String = "", // The origin station number
        @SerializedName("polyline")
        val polyline: String = "",
        @SerializedName("price")
        val price: Double = 0.0, // The price for this ride
        @SerializedName("product_id")
        val productId: String = "",
        @SerializedName("product_name")
        val productName: String = "",
        @SerializedName("short_name")
        val shortName: String = "", // The name of the ride
        @SerializedName("short_name_no")
        val shortNameNo: String = "", // Number of the ride
        @SerializedName("started_on")
        val startedOn: Int = 0, // Departure time of the ride
        @SerializedName("steps_detail")
        val stepsDetail: List<StepsDetail> = listOf(),
        @SerializedName("ticket_status")
        val ticketStatus: String = "",
        @SerializedName("ticket_uuid")
        val ticketUuid: String = ""
    ) {
        fun oriLatLng() = LatLng(originLat,originLng)
        fun destinationLatLng() = LatLng(destinationLat,destinationLng)

        data class StepsDetail(
            @SerializedName("arrival_stop")
            val arrivalStop: ArrivalStop = ArrivalStop(),
            @SerializedName("arrival_time")
            val arrivalTime: ArrivalTime = ArrivalTime(),
            @SerializedName("building_level")
            val buildingLevel: BuildingLevel = BuildingLevel(),
            @SerializedName("departure_stop")
            val departureStop: DepartureStop = DepartureStop(),
            @SerializedName("departure_time")
            val departureTime: DepartureTime = DepartureTime(),
            @SerializedName("distance")
            val distance: Distance = Distance(),
            @SerializedName("duration")
            val duration: Duration = Duration(),
            @SerializedName("end_location")
            val endLocation: EndLocation = EndLocation(),
            @SerializedName("headsign")
            val headsign: String = "",
            @SerializedName("html_instructions")
            val htmlInstructions: String = "",
            @SerializedName("line")
            val line: Line = Line(),
            @SerializedName("maneuver")
            val maneuver: String = "",
            @SerializedName("num_stops")
            val numStops: Int = 0,
            @SerializedName("polyline")
            val polyline: Polyline = Polyline(),
            @SerializedName("start_location")
            val startLocation: StartLocation = StartLocation(),
            @SerializedName("travel_mode")
            val travelMode: String = "", // Which travel mode is this (options: 2=public transit, 5=intermodal)
            @SerializedName("trip_short_name")
            val tripShortName: String = ""
        ) {
            data class ArrivalStop(
                @SerializedName("location")
                val location: LocationStop = LocationStop(),
                @SerializedName("name")
                val name: String = ""
            )


            data class ArrivalTime(
                @SerializedName("text")
                val text: String = "",
                @SerializedName("time_zone")
                val timeZone: String = "",
                @SerializedName("value")
                val value: Int = 0
            )

            data class BuildingLevel(
                @SerializedName("number")
                val number: Int = 0
            )

            data class DepartureStop(
                @SerializedName("location")
                val location: LocationStop = LocationStop(),
                @SerializedName("name")
                val name: String = ""
            )

            data class DepartureTime(
                @SerializedName("text")
                val text: String = "",
                @SerializedName("time_zone")
                val timeZone: String = "",
                @SerializedName("value")
                val value: Int = 0
            )

            data class Distance(
                @SerializedName("text")
                val text: String = "",
                @SerializedName("value")
                val value: Int = 0
            )

            data class Duration(
                @SerializedName("text")
                val text: String = "",
                @SerializedName("value")
                val value: Int = 0
            )

            data class EndLocation(
                @SerializedName("lat")
                val lat: Double = 0.0,
                @SerializedName("lng")
                val lng: Double = 0.0
            )




            data class Line(
                @SerializedName("agencies")
                val agencies: List<Agency> = listOf(),
                @SerializedName("color")
                val color: String = "",
                @SerializedName("name")
                val name: String = "",
                @SerializedName("short_name")
                val shortName: String = "",
                @SerializedName("text_color")
                val textColor: String = "",
                @SerializedName("url")
                val url: String = "",
                @SerializedName("vehicle")
                val vehicle: Vehicle = Vehicle()
            ){


                data class Agency(
                    @SerializedName("name")
                    val name: String = "",
                    @SerializedName("phone")
                    val phone: String = "",
                    @SerializedName("url")
                    val url: String = ""
                )

                data class Vehicle(
                    @SerializedName("icon")
                    val icon: String = "",
                    @SerializedName("name")
                    val name: String = "",
                    @SerializedName("type")
                    val type: String = ""
                )
            }

            data class Polyline(
                @SerializedName("points")
                val points: String = ""
            )

            data class StartLocation(
                @SerializedName("lat")
                val lat: Double = 0.0,
                @SerializedName("lng")
                val lng: Double = 0.0
            )

            data class LocationStop(
                @SerializedName("lat")
                val lat: Double = 0.0,
                @SerializedName("lng")
                val lng: Double = 0.0
            )



        }
    }
}
