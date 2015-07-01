package ca.uwaterloo.Lab4_205_05;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.TextView;

class MagnetoSensorEventListener implements SensorEventListener {
	public static double angle = 0.00;
	
	float x,y,z;
	TextView output;
	TextView stepView;
	float maxMagx;
	float maxMagy;
	float maxMagz;
	float[] geomagnetic;
	float[] angleReading = new float[2];
	public static double northSteps, eastSteps, netDis, netDir = 0;
	
	public MagnetoSensorEventListener(TextView outputText, TextView stepsInNE){
		output = outputText;
		stepView = stepsInNE;
	}
	
	
	
		public void onAccuracyChanged(Sensor s, int i){}
		
		public void onSensorChanged(SensorEvent se){
			if (se.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
							
				x = se.values[0];
				y = se.values[1];
				z = se.values[2];
				geomagnetic = se.values;
				double properAngle = 0;
				
				if( geomagnetic != null && MainActivity.gravity != null){

					float[] R = new float[9];
					float[] I = new float[9];
					
					boolean success = SensorManager.getRotationMatrix(R, I,MainActivity.gravity, geomagnetic);
					if(success){
						float orientation[] = new float[3];	
						SensorManager.getOrientation(R, orientation);
						angleReading[0] = (float)orientation[0];
						angle = Math.round(-angleReading[0]*180/3.1415926f);
						if(angle < 0){
							properAngle = -angle;
						}
						else{
							properAngle = 360 - angle;
						}
						if(AccelerometerSensorEventListener.stepTrue){
							northSteps += 1*Math.cos((properAngle*3.1415926f/180));
							eastSteps += 1*Math.sin((properAngle*3.1415926f/180));
							
							AccelerometerSensorEventListener.stepTrue = false;
						}
										
						netDis = Math.sqrt(Math.pow(northSteps, 2)+ Math.pow(eastSteps, 2));
						netDir = (Math.atan(eastSteps/northSteps))*180/3.1415926f;
						
					/*	if(netDir < 0)
						{
							netBer = 360 + netDir;
						}
						else if(netDir > 0)
						{
							netBer = netDir;
						}
						else
						{
							netBer = 0;
						}
					*/	
						output.setTextSize(30);
						output.setText("North " + String.valueOf(properAngle));
						stepView.setText("Steps north : " + String.format("%.2f", northSteps)  + "\nSteps east : " +  String.format("%.2f", eastSteps)
								+ "\nNet Displacement: " + String.format("%.2f", netDis) + " (" + String.format("%.2f", netDir) + ")");
					}
				}
				
				/*
				if(Math.abs(se.values[0]) > maxMagx ) {maxMagx = Math.abs(se.values[0]);}
				if(Math.abs(se.values[1]) > maxMagy ) {maxMagy = Math.abs(se.values[1]);}
				if(Math.abs(se.values[2]) > maxMagz ) {maxMagz = Math.abs(se.values[2]) ;}
				
				String[] num = new String[] {(String.format("%.3f", x)), (String.format("%.3f", y)), (String.format("%.3f", z))};
				//String maxValues = String.format("(%.3f, %.3f, %.3f)", maxMagx, maxMagy, maxMagz);
				
				output.setText("MAGNETOMETER (uT) \n        X: " + num[0] + "\n"
						+ "        Y: " + num[1] + "\n        Z: " + num[2] +"\n"); 
				*/
							
				}
		}
}