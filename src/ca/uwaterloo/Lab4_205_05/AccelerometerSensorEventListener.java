package ca.uwaterloo.Lab4_205_05;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.widget.TextView;

class AccelerometerSensorEventListener implements SensorEventListener {
	public static float rotVal0;
	public static float rotVal1;
	float x,y,z,previous;
	TextView output;
	float[] maxAcc, minAcc = new float[3];
	float[] outputA = new float[3];
	double C = 6.0;
	int state = 0; 
	String[] num;
	String maxValues, minValues;
	public static boolean stepTrue = false;
	
	public AccelerometerSensorEventListener(TextView outputText){
		output = outputText;		
		}		

		public void onAccuracyChanged(Sensor s, int i){}
		
		public void onSensorChanged(SensorEvent se){

			MainActivity.gravity = se.values;
			
				outputA[0] += (se.values[0] - outputA[0]) / C;
				outputA[1] += (se.values[1] - outputA[1]) / C;
				outputA[2] += (se.values[2] - outputA[2]) / C;
			
			if (se.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
				
				x = se.values[0];
				y = se.values[1];
				z = se.values[2];
		
				MainActivity.graph.addPoint(outputA);
			}
			
			// lower-bound 0.6 & C = 6 and GAME  (steps are perfect)
			
			if( rotVal1 < 0.34 &&  rotVal0 < 0.2 ){
				if (state == 0 && outputA[2]> 9.78 ){
					state = 1; 
				}
				else if (state == 0){
					state=0;
				}
				if(state == 1 && outputA[2] > 13.78){
					state = 0;
				}
				else if (state ==1 && previous>outputA[2] && outputA[2] > 10.38){
					state=2; 
				}
				else if (state==2 && outputA[2] < 9.78 ){
					state =3; 
				}
				else if (state==2 && previous<outputA[2]){
					state =0; 
				}
				if (state==3 && previous<outputA[2]){
					state =4; 
				}
				if (state==4 && outputA[2]> 9.78 ){
					state =0;
					MainActivity.steps++;
					stepTrue = true;
				}
				else if (state==4 && previous > outputA[2]){
					state =0; 
				}
			}
			previous = outputA[2];
			
				output.setText("Threshold = " + C + "  Total steps: " + MainActivity.steps);
		}
		public void barChanged(double value)
		{
			this.C = value;
		}
	}

