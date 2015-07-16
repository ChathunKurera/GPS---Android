package ca.uwaterloo.Lab4_205_05;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import android.graphics.PointF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.TextView;



class MagnetoSensorEventListener implements SensorEventListener {
	public static double angle = 0;
	
	float x,y,z;
	TextView output;
	TextView stepView;
	float[] geomagnetic;
	static float[] angleReading = new float[2];
	List<PointF> userPathPts = new ArrayList<PointF>();
	public static double northSteps, eastSteps, netDis, netDir = 0;
	double error;
	static double addedXdirection;
	static double addedYdirection;
	static double stepSize = 1.5;
	MapView mv;
	PositionListener pl;
	
	public MagnetoSensorEventListener(TextView outputText, TextView stepsInNE, MapView mv, PositionListener pl){
		output = outputText;
		stepView = stepsInNE;
		this.pl = pl;
		this.mv = mv;
		
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
							northSteps += Math.cos((properAngle*3.1415926f/180));
							eastSteps += Math.sin((properAngle*3.1415926f/180));

							if(MainActivity.startPoint != null){
								addedXdirection = (double) stepSize*Math.sin(Math.PI - angleReading[0] - 0.33);
								addedYdirection = (double) stepSize*Math.cos(Math.PI - angleReading[0] - 0.30);
								updateUserPoint(addedXdirection, addedYdirection);								
								MainActivity.mv.setUserPoint(MainActivity.userP);	
								
							}

							
							
							MainActivity.nextToUser = new PointF((float) (MainActivity.userP.x + 
						    		Math.sin(Math.PI - MagnetoSensorEventListener.angleReading[0] - 0.289)), (float) (MainActivity.userP.y +
						    		Math.cos(Math.PI - MagnetoSensorEventListener.angleReading[0] - 0.349)));
							
						    userPathPts.add(MainActivity.userP);
							userPathPts.add(MainActivity.nextToUser);
							MainActivity.mv.setUuPath(userPathPts);
							MainActivity.mv.removeLabeledPoint(MainActivity.nextToUser);
							userPathPts.remove(MainActivity.nextToUser);
							userPathPts.clear();
							

							MainActivity.pathPoints.add(MainActivity.userP);

							MainActivity.mv.setUserPath(MainActivity.pathPoints);
							
							pl.destinationChanged(mv, MainActivity.endPoint);
							MainActivity.pathPoints.clear();	
							//MainActivity.pathPoints.remove(MainActivity.userP);
							//MainActivity.pathPoints.remove(MainActivity.closestToS);
							//MainActivity.pathPoints.remove(MainActivity.endPoint);
							//MainActivity.pathPoints.remove(MainActivity.closestToE);
							
							double mmm = Math.abs(MainActivity.userP.x-MainActivity.endPoint.x);
							double mmmm = Math.abs(MainActivity.userP.y-MainActivity.endPoint.y);
							if(Math.sqrt(Math.pow(mmm, 2)+Math.pow(mmmm, 2)) < 1.7){
								MainActivity.turnDir.setText(null);
								MainActivity.turnDir.setText("Voila! You arrived at your destination");
							}
							
							
							AccelerometerSensorEventListener.stepTrue = false;	
						}							
						
						netDis = Math.sqrt(Math.pow(northSteps, 2)+ Math.pow(eastSteps, 2));
						error = netDis/MainActivity.steps *100;
						
						output.setTextSize(30);
						output.setTextColor(Color.RED);
						output.setText("North " + String.valueOf(properAngle));
						stepView.setText("North : " + String.format("%.2f", northSteps)  + "   East : " +  String.format("%.2f", eastSteps)
								+ "\nNet Displacement: " + String.format("%.2f", netDis) + "\nError " 
								+ String.format("%.2f", error)+"%");
						
					}
				}
				
			}
		}
		
		
		
		public static void updateUserPoint(double addedX, double addedY){
			//if(MainActivity.map.calculateIntersections(MainActivity.userP, MainActivity.nextToUser).size() == 0){
			MainActivity.userP.x = (float) addedX + MainActivity.userP.x;
			MainActivity.userP.y = (float) addedY + MainActivity.userP.y;
			
	    }
		
}