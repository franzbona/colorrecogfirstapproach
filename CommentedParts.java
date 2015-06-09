// //Commented part for picking the color to detect
		// Scanner in = new Scanner(System.in);
		//
		// while (!s.equals("red") && !s.equals("blue") && !s.equals("green")) {
		//
		// System.out.println("Which colour would you like to detect?");
		// s = in.nextLine().toLowerCase();
		// System.out.println("You entered " + s);
		// }
		// in.close();


// //Commented part with the IFs to detect the range of the respective
		// color
		// if (s.equals("red")) {
		//
		// CvScalar red_min = cvScalar(0, 0, 130, 0);
		// CvScalar red_max = cvScalar(80, 80, 255, 0);
		// cvInRangeS(orgImg, red_min, red_max, imgThreshold);// red
		//
		// }
		//
		// if (s.equals("blue")) {
		//
		// CvScalar blue_min = cvScalar(130, 0, 0, 0);
		// CvScalar blue_max = cvScalar(255, 80, 80, 0);
		// cvInRangeS(orgImg, blue_min, blue_max, imgThreshold);// blue
		// }
		// if (s.equals("green")) {
		//
		// CvScalar green_min = cvScalar(0, 130, 0, 0);
		// CvScalar green_max = cvScalar(80, 255, 80, 0);
		// cvInRangeS(orgImg, green_min, green_max, imgThreshold);// green
		// }



//// DETECT IF BLUE-ISH
//		if (avg.blue() >= avg.green() && avg.blue() >= avg.red()) {
//
//			double min_b;
//			if ((avg.blue() - range) < 0)
//				min_b = 0;
//			else
//				min_b = (avg.blue() - range);
//
//			double max_b;
//			if ((avg.blue() + range) > 255)
//				max_b = 255;
//			else
//				max_b = (avg.blue() + range);
//
//			CvScalar blue_min = cvScalar(min_b, avg.green(), avg.red(), 0);
//			CvScalar blue_max = cvScalar(max_b, avg.green(), avg.red(), 0);
//			cvInRangeS(orgImg, blue_min, blue_max, imgThreshold);
//
//		}
//
//		// DETECT IF GREEN-ISH
//		else if (avg.green() >= avg.red() && avg.green() >= avg.blue()) {
//
//			double min_g;
//			if ((avg.green() - range) < 0)
//				min_g = 0;
//			else
//				min_g = (avg.green() - range);
//
//			double max_g;
//			if ((avg.green() + range) > 255)
//				max_g = 255;
//			else
//				max_g = (avg.green() + range);
//
//			CvScalar green_min = cvScalar(avg.blue(), min_g, avg.red(), 0);
//			CvScalar green_max = cvScalar(avg.blue(), max_g, avg.red(), 0);
//			cvInRangeS(orgImg, green_min, green_max, imgThreshold);
//
//		}
//
//		// DETECT IF RED-ISH
//		else if (avg.red() >= avg.green() && avg.red() >= avg.blue()) {
//
//			double min_r;
//			if ((avg.red() - range) < 0)
//				min_r = 0;
//			else
//				min_r = (avg.red() - range);
//
//			double max_r;
//			if ((avg.red() + range) > 255)
//				max_r = 255;
//			else
//				max_r = (avg.red() + range);
//
//			CvScalar red_min = cvScalar(avg.blue(), avg.green(), min_r, 0);
//			CvScalar red_max = cvScalar(avg.blue(), avg.green(), max_r, 0);
//			cvInRangeS(orgImg, red_min, red_max, imgThreshold);
//
//		}
