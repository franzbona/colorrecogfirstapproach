/*
 * Just an example using JavaCV to make a colored object tracking
 * SOURCE: https://github.com/bytedeco/javacv/blob/master/samples/ColoredObjectTrack.java
 * I adapted it to create a link between the points detected from the camera
 */

import static org.bytedeco.javacpp.opencv_core.IPL_DEPTH_8U;
import static org.bytedeco.javacpp.opencv_core.cvFlip;
import static org.bytedeco.javacpp.opencv_core.cvGetSize;
import static org.bytedeco.javacpp.opencv_core.cvInRangeS;
import static org.bytedeco.javacpp.opencv_core.cvScalar;
import static org.bytedeco.javacpp.opencv_highgui.cvSaveImage;
import static org.bytedeco.javacpp.opencv_imgproc.CV_BGR2GRAY;
import static org.bytedeco.javacpp.opencv_imgproc.CV_MEDIAN;
import static org.bytedeco.javacpp.opencv_imgproc.cvCvtColor;
import static org.bytedeco.javacpp.opencv_imgproc.cvEqualizeHist;
import static org.bytedeco.javacpp.opencv_imgproc.cvGetCentralMoment;
import static org.bytedeco.javacpp.opencv_imgproc.cvGetSpatialMoment;
import static org.bytedeco.javacpp.opencv_imgproc.cvMoments;
import static org.bytedeco.javacpp.opencv_imgproc.cvSmooth;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.Scanner;

import javax.swing.JPanel;

import org.bytedeco.javacpp.opencv_core.CvScalar;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_imgproc.CvMoments;
import org.bytedeco.javacpp.helper.opencv_core.AbstractIplImage;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;

public class ColoredObjectTrack implements Runnable {

	public static void main(String[] args) {
		ColoredObjectTrack cot = new ColoredObjectTrack();
		Thread th = new Thread(cot);
		th.start();
	}

	int count = 0;
	int oldposX = 0;
	int oldposY = 0;
	char color = 'n';
	String s = "";
	final int INTERVAL = 10;// 1sec
	final int CAMERA_NUM = 0; // Default camera for this time

	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	CanvasFrame canvas = new CanvasFrame("Web Cam Live");
	CanvasFrame path = new CanvasFrame("Detection");
	JPanel jp = new JPanel();

	public ColoredObjectTrack() {

		canvas.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
		canvas.setLocation(0, 0);
		path.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
		path.setLocation((int) screenSize.getWidth() / 2, 0);
		path.setContentPane(jp);

		Scanner in = new Scanner(System.in);

		//asks users which color to track
		while (!s.equals("red") && !s.equals("blue") && !s.equals("green")) {

			System.out.println("Which colour would you like to detect?");
			s = in.nextLine().toLowerCase();
			System.out.println("You entered " + s);
		}
		in.close();

	}

	private void paint(IplImage img, int posX, int posY) {

		count++;
		Graphics g = jp.getGraphics();
		path.setSize(canvas.getWidth(), canvas.getHeight());

		if (color == 'r')
			g.setColor(Color.RED);

		if (color == 'b')
			g.setColor(Color.BLUE);

		if (color == 'g')
			g.setColor(Color.GREEN);

		g.fillOval(posX, posY, 5, 5);
		System.out.println(posX + " , " + posY);

		//checks if already 1 point is drawn
		if (count > 1) {
			g.drawLine(posX, posY, oldposX, oldposY);
		}
		
		//updates point location
		oldposX = posX;
		oldposY = posY;

	}

	private IplImage getThresholdImage(IplImage orgImg) {
		IplImage imgThreshold = AbstractIplImage
				.create(cvGetSize(orgImg), 8, 1);

		//checks color and detects it if in range
		if (s.equals("red")) {

			color = 'r';
			CvScalar red_min = cvScalar(0, 0, 130, 0);
			CvScalar red_max = cvScalar(80, 80, 255, 0);
			cvInRangeS(orgImg, red_min, red_max, imgThreshold);// red

		}

		if (s.equals("blue")) {

			color = 'b';
			CvScalar blue_min = cvScalar(130, 0, 0, 0);
			CvScalar blue_max = cvScalar(255, 80, 80, 0);
			cvInRangeS(orgImg, blue_min, blue_max, imgThreshold);// blue
		}
		if (s.equals("green")) {

			color = 'g';
			CvScalar green_min = cvScalar(0, 130, 0, 0);
			CvScalar green_max = cvScalar(80, 255, 80, 0);
			cvInRangeS(orgImg, green_min, green_max, imgThreshold);// green
		}

		//smoothes the image according to the parameters
		cvSmooth(imgThreshold, imgThreshold, CV_MEDIAN, 15, 0, 0, 0);
		cvSaveImage("dsmthreshold.jpg", imgThreshold);
		return imgThreshold;
	}

	public IplImage Equalize(BufferedImage bufferedimg) {

		Java2DFrameConverter converter1 = new Java2DFrameConverter();
		OpenCVFrameConverter.ToIplImage converter2 = new OpenCVFrameConverter.ToIplImage();
		IplImage iploriginal = converter2.convert(converter1
				.convert(bufferedimg));
		IplImage srcimg = AbstractIplImage.create(iploriginal.width(),
				iploriginal.height(), IPL_DEPTH_8U, 1);
		IplImage destimg = AbstractIplImage.create(iploriginal.width(),
				iploriginal.height(), IPL_DEPTH_8U, 1);
		cvCvtColor(iploriginal, srcimg, CV_BGR2GRAY);
		cvEqualizeHist(srcimg, destimg);
		return destimg;
	}

	public void run() {
		try {
			FrameGrabber grabber = FrameGrabber.createDefault(CAMERA_NUM);
			OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();
			grabber.start();
			IplImage img;
			int posX = 0;
			int posY = 0;
			while (true) {
				img = converter.convert(grabber.grab());
				if (img != null) {
					// show image on window
					cvFlip(img, img, 1);
					canvas.showImage(converter.convert(img));
					IplImage detectThrs = getThresholdImage(img);

					CvMoments moments = new CvMoments();
					cvMoments(detectThrs, moments, 1);
					double mom10 = cvGetSpatialMoment(moments, 1, 0);
					double mom01 = cvGetSpatialMoment(moments, 0, 1);
					double area = cvGetCentralMoment(moments, 0, 0);
					posX = (int) (mom10 / area);
					posY = (int) (mom01 / area);
					// only if its a valid position
					if (posX > 0 && posY > 0) {
						paint(img, posX, posY);
					}
				}
				// Thread.sleep(INTERVAL);
			}
		} catch (Exception e) {
		}
	}
}