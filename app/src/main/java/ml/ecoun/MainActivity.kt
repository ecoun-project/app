package ml.ecoun

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.TextView

import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*

class MainActivity : AppCompatActivity() {


    //Declaring the needed Variables
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    val PERMISSION_ID = 1010



    var txt:TextView?=null
    var button:Button?=null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        txt = findViewById(R.id.txtlocation)
        button = findViewById(R.id.btn)

        //init
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        button!!.setOnClickListener{
            Log.d("Debug:",CheckPermission().toString())
            Log.d("Debug:",isLocationEnabled().toString())
            RequestPermission()
            getLastLocation()
        }

    }
    //funtion get the last location
    private fun getLastLocation(){

        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission( this,  Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            System.out.println("entra")

            if(CheckPermission()){

                //location service is enabled
                if(isLocationEnabled()){
                    //Get the location
                    fusedLocationProviderClient.lastLocation.addOnCompleteListener {task->
                        var location = task.result
                        if(location==null){
                            getNewLocation()
                        }else{
                            //present location
                            txt!!.text = "Tu ubicacion es \n Lat :"+location.latitude + "\n Long:"+location.longitude
                            System.out.println("Tu ubicacion es \n Lat :"+location.latitude + "\n Long:"+location.longitude )
                        }
                    }
                }else{
                    Toast.makeText(this,"Enciente el GPS",Toast.LENGTH_LONG).show()
                }
            }else{
                System.out.println("no entra")
            }
        }


    }

    private fun getNewLocation(){
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            locationRequest = LocationRequest()
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            locationRequest.interval = 0
            locationRequest.fastestInterval = 0
            locationRequest.numUpdates =2

            fusedLocationProviderClient!!.requestLocationUpdates(
                locationRequest,locationCallback,Looper.myLooper()
            )
        }

    }
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            var lastLocation =p0.lastLocation
            txt!!.text = "Tu ubicacion es \n Lat :"+lastLocation.latitude + "\n Long:"+lastLocation.longitude
        }
    }

    //Check permisiion
    private fun CheckPermission():Boolean{
        if(
            ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ){
            return true
        }

        return false
    }

    //user permisiion

    fun RequestPermission(){
        //this function will allows us to tell the user to requesut the necessary permsiion if they are not garented
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_ID
        )
    }

    fun isLocationEnabled():Boolean{
        //this function will return to us the state of the location service
        //if the gps or the network provider is enabled then it will return true otherwise it will return false
        var locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        //Check de permission result
        if(requestCode == PERMISSION_ID){
            if(grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Log.d("Debug:","Tienes permiso")
            }
        }
    }
}