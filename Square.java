/** Original from https://github.com/bytedeco/javacv/blob/master/samples/Square.java */

import org.bytedeco.javacpp.*;
import org.bytedeco.javacpp.opencv_core.IplImage;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;
import static org.bytedeco.javacpp.opencv_highgui.*;

public class Square {

	int thresh = 50;
	IplImage img = null;
	IplImage orgImg = null;
	IplImage finImg = null;
	CvMemStorage storage = null;
	String filename = "";

	public static void main(String args[]) {
		new Square().main();
	}

	public void main() {
		// create memory storage that will contain all the dynamic data
		storage = cvCreateMemStorage(0);

		// filename = "ColorFades.jpg";
		// filename = "ColorWall.jpg";
		filename = "Pixels.jpg";
		// filename = "Squares.jpg";
		// filename = "Points.jpg";
		// filename = "Rainbow.jpg";
		// filename = "RGB.jpg";
		// filename = "Wheel.jpg";

		orgImg = cvLoadImage(filename);
		img = cvCloneImage(orgImg);

		// find and draw the squares
		drawSquares(img, findSquares4(img, storage));

		// clear memory storage - reset free space position
		cvClearMemStorage(storage);
	}

	// helper function:
	// finds a cosine of angle between vectors
	// from pt0->pt1 and from pt0->pt2
	double angle(CvPoint pt1, CvPoint pt2, CvPoint pt0) {
		double dx1 = pt1.x() - pt0.x();
		double dy1 = pt1.y() - pt0.y();
		double dx2 = pt2.x() - pt0.x();
		double dy2 = pt2.y() - pt0.y();

		return (dx1 * dx2 + dy1 * dy2)
				/ Math.sqrt((dx1 * dx1 + dy1 * dy1) * (dx2 * dx2 + dy2 * dy2)
						+ 1e-10);
	}

	// returns sequence of squares detected on the image.
	// the sequence is stored in the specified memory storage
	CvSeq findSquares4(IplImage img, CvMemStorage storage) {

		int i, c, l, N = 11;
		CvSize sz = cvSize(img.width() & -2, img.height() & -2);
		IplImage timg = cvCloneImage(img); // make a copy of input image
		IplImage gray = cvCreateImage(sz, 8, 1);
		IplImage pyr = cvCreateImage(cvSize(sz.width() / 2, sz.height() / 2),
				8, 3);
		IplImage tgray = null;

		// create empty sequence that will contain points -
		// 4 points per square (the square's vertices)
		CvSeq squares = cvCreateSeq(0, Loader.sizeof(CvSeq.class),
				Loader.sizeof(CvPoint.class), storage);

		// select the maximum ROI in the image
		// with the width and height divisible by 2
		cvSetImageROI(timg, cvRect(0, 0, sz.width(), sz.height()));

		// down-scale and upscale the image to filter out the noise
		cvPyrDown(timg, pyr, 7);
		cvPyrUp(pyr, timg, 7);
		tgray = cvCreateImage(sz, 8, 1);

		// find squares in every color plane of the image
		for (c = 0; c < 3; c++) {
			// extract the c-th color plane
			cvSetImageCOI(timg, c + 1);
			cvCopy(timg, tgray);

			// try several threshold levels
			for (l = 0; l < N; l++) {
				if (l == 0) {
					// apply Canny. Take the upper threshold from slider
					// and set the lower to 0 (which forces edges merging)
					cvCanny(tgray, gray, 0, thresh, 5);
					// dilate canny output to remove potential
					// holes between edge segments
					cvDilate(gray, gray, null, 1);
				} else {
					// apply threshold if l!=0:
					// tgray(x,y) = gray(x,y) < (l+1)*255/N ? 255 : 0
					cvThreshold(tgray, gray, (l + 1) * 255 / N, 255,
							CV_THRESH_BINARY);
				}

				// find contours and store them all as a list
				// Java translation: moved into the loop
				CvSeq contours = new CvSeq();
				cvFindContours(gray, storage, contours,
						Loader.sizeof(CvContour.class), CV_RETR_LIST,
						CV_CHAIN_APPROX_SIMPLE, cvPoint(0, 0));

				// test each contour
				while (contours != null && !contours.isNull()) {
					// approximate contour with accuracy proportional
					// to the contour perimeter
					// Java translation: moved into the loop
					CvSeq result = cvApproxPoly(contours,
							Loader.sizeof(CvContour.class), storage,
							CV_POLY_APPROX_DP,
							cvContourPerimeter(contours) * 0.02, 0);
					// square contours should have 4 vertices after
					// approximation relatively large area
					// (to filter out noisy contours) and be convex.
					if (result.total() == 4
							&& Math.abs(cvContourArea(result, CV_WHOLE_SEQ, 0)) > 1000
							&& cvCheckContourConvexity(result) != 0) {

						// Java translation: moved into loop
						double s = 0.0, t = 0.0;

						for (i = 0; i < 5; i++) {
							// find minimum angle between joint
							// edges (maximum of cosine)
							if (i >= 2) {
								t = Math.abs(angle(
										new CvPoint(cvGetSeqElem(result, i)),
										new CvPoint(cvGetSeqElem(result, i - 2)),
										new CvPoint(cvGetSeqElem(result, i - 1))));
								s = s > t ? s : t;
							}
						}

						// if cosines of all angles are small
						// (all angles are ~90 degree) then write quandrange
						// vertices to resultant sequence
						if (s < 0.3)
							for (i = 0; i < 4; i++) {
								cvSeqPush(squares, cvGetSeqElem(result, i));
							}
					}

					// take the next contour
					contours = contours.h_next();
				}
			}
		}

		return squares;

	}

	// the function draws all the squares in the image
	void drawSquares(IplImage img, CvSeq squares) {

		IplImage cpy = cvCloneImage(img);
		int i = 0;

		// Create a "super"-slice, consisting of the entire sequence of squares
		CvSlice slice = new CvSlice(squares);

		// read 4 sequence elements at a time (all vertices of a square)
		for (i = 0; i < squares.total(); i += 4) {

			// This works, may be the "cleanest" solution, does not use the
			// "reader"
			CvPoint rect = new CvPoint(4);
			IntPointer count = new IntPointer(1).put(4);
			// get the 4 corner slice from the "super"-slice
			cvCvtSeqToArray(squares, rect, slice.start_index(i)
					.end_index(i + 4));

			// draw the square as a closed polyline
			cvPolyLine(cpy, rect.position(0), count, 1, 1, CV_RGB(0, 255, 0),
					3, CV_AA, 0);
		}

		finImg = cvCreateImage(cvGetSize(orgImg), 8, 1);
		cvSaveImage("thr_" + filename, finImg);
	}

}