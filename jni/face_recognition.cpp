#include <jni.h>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/features2d/features2d.hpp>
#include <opencv2/contrib/contrib.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <vector>
#include <fstream>
#include <sstream>
#include <android/log.h>
#define LOG_TAG ":: face_recognition ::"
#define LOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__))

// Referência
// http://docs.opencv.org/3.0-beta/modules/face/doc/facerec/tutorial/facerec_video_recognition.html

using namespace std;
using namespace cv;

extern "C" {
JNIEXPORT void JNICALL Java_br_com_wsilva_opencvfacerecognition_util_FaceRecognition_FindFeatures(JNIEnv* env,
	jobject, jint width, jint height, jbyteArray yuv, jintArray bgra)
{
	try
	{
		jbyte* _yuv = env->GetByteArrayElements(yuv, 0);
		jint* _bgra = env->GetIntArrayElements(bgra, 0);

		Mat myuv(height + height/2, width, CV_8UC1, (unsigned char *)_yuv);
		Mat mbgra(height, width, CV_8UC4, (unsigned char *)_bgra);
		Mat mgray(height, width, CV_8UC1, (unsigned char *)_yuv);

		cvtColor(myuv, mbgra, CV_YUV420sp2BGR, 4);

		env->ReleaseIntArrayElements(bgra, _bgra, 0);
		env->ReleaseByteArrayElements(yuv, _yuv, 0);
	} catch(cv::Exception e)
	{
		LOGD("FindFeatures::cv::Exception: %s", e.what());
		jclass je = env->FindClass("org/opencv/core/CvException");
		if(!je)
		je = env->FindClass("java/lang/Exception");
		env->ThrowNew(je, e.what());
	}
	catch (...)
	{
		LOGD("Exceção não conhecida.");
		jclass je = env->FindClass("java/lang/Exception");
		env->ThrowNew(je, "Unknown exception in JNI code {highgui::VideoCapture_n_1VideoCapture__()}");
	}
}

/**
 * jImageName - Nome da imagem a ser utilizada para encontrar a Face
 * jFileName - Nome do arquivo HAAR (CascadeClassifier)
 */
JNIEXPORT void JNICALL Java_br_com_wsilva_opencvfacerecognition_util_FaceRecognition_FindFaces(JNIEnv* env,
	jobject, jstring jImageName,jstring jFileName)
{
	try
	{
		const char* jnamestr = env->GetStringUTFChars(jFileName, NULL);
		string stdFileName(jnamestr);
		const char* jimagestr = env->GetStringUTFChars(jImageName, NULL);
		string stdImageName(jimagestr);

		LOGD("Imagem a ser tratada = %s ",stdImageName.c_str());

		CascadeClassifier haar_cascade;
		//Carrega o classifier a partir do arquivo fornecido
		if(haar_cascade.load(stdFileName))
		{
			LOGD("HAAR carregado com sucesso.");
		}
		LOGD("CascadeClassifier Filename : %s ",stdFileName.c_str());

		//Imagem a ser carregada para encontrar a Face
		Mat original = imread(stdImageName + ".jpg", 1);
		if(! original.data )
		{
		    LOGD("Não foi possível carregar a imagem.");
		}

		Mat gray ;
		cvtColor(original,gray,CV_BGR2GRAY);
		vector< Rect_<int> > faces;
		haar_cascade.detectMultiScale(gray, faces, 1.1, 3, 0, Size(20,60));

		LOGD("Quantidade de Faces encontradas : %d ",faces.size());
		if(faces.size()>0)
		{
			Rect face_i = faces[0];
			Mat original_face = original(face_i);
			//Alinhar a imagem
			resize(original_face,original_face,Size(200,200));
			imwrite(stdImageName + "_det.jpg",original_face);
		}

		return;
	} catch(cv::Exception e)
	{
		LOGD("FindFaces::cv::Exception: %s", e.what());
		jclass je = env->FindClass("org/opencv/core/CvException");
		if(!je)
		je = env->FindClass("java/lang/Exception");
		env->ThrowNew(je, e.what());
	}
	catch (...)
	{
		LOGD("Exceção não conhecida.");
		jclass je = env->FindClass("java/lang/Exception");
		env->ThrowNew(je, "Unknown exception in JNI code {highgui::VideoCapture_n_1VideoCapture__()}");
	}
}

/**
 * jImageName 	- Nome da imagem a ser utilizada para encontrar a Face
 * jFileName 	- Nome do arquivo HAAR (CascadeClassifier)
 * jCsv			- Nome do arquivo CSV
 */
JNIEXPORT int JNICALL Java_br_com_wsilva_opencvfacerecognition_util_FaceRecognition_Find(JNIEnv* env,
	jobject, jstring jImageName,jstring jFileName,jstring jCsv)
{
	try
	{
		const char* jnamestr = env->GetStringUTFChars(jFileName, NULL);
		string stdFileName(jnamestr);
		const char* jimagestr = env->GetStringUTFChars(jImageName, NULL);
		string stdImageName(jimagestr);
		const char* jCsvstr = env->GetStringUTFChars(jCsv, NULL);
		string stdCsv(jCsvstr);

		vector<Mat> images;
		vector<int> labels;
		std::ifstream file(stdCsv.c_str(), ifstream::in);

		LOGD("Imagem carregada = %s ",stdImageName.c_str());

		string line, path, classlabel;
		while (getline(file, line))
		{
			stringstream liness(line);
			getline(liness, path, ';');
			getline(liness, classlabel);
			if(!path.empty() && !classlabel.empty())
			{
				images.push_back(imread(path, 0));
				labels.push_back(atoi(classlabel.c_str()));
			}
		}

		// Get the height from the first image. We'll need this
		// later in code to reshape the images to their original
		// size AND we need to reshape incoming faces to this size:
		int im_width = images[0].cols;
		int im_height = images[0].rows;

		// Create a FaceRecognizer and train it on the given images
		Ptr<FaceRecognizer> model = createEigenFaceRecognizer();
		model->train(images, labels);

		CascadeClassifier haar_cascade;
		haar_cascade.load(stdFileName);
		for(;;)
		{
			Mat original = imread(stdImageName, 1);
			//Mat original = imread(stdImageName+".jpg", 1);
			// Clone the current frame:
			Mat gray = original.clone();
			// Convert the current frame to grayscale:
			cvtColor(original,gray,CV_BGR2GRAY);
			// Find the faces in the frame:
			vector< Rect_<int> > faces;
			haar_cascade.detectMultiScale(gray, faces, 1.1, 3, 0, Size(20,60));
			LOGD("Número de Faces = %d",faces.size());

			for(int i = 0; i < faces.size(); i++)
			{
				// Process face by face:
				Rect face_i = faces[i];
				// Crop the face from the image.
				Mat face = gray(face_i);
				Mat face_resized;
				cv::resize(face, face_resized, Size(im_width, im_height), 1.0, 1.0, INTER_CUBIC);
				// Now perform the prediction
				double predicted_confidence = 0.0;
				int prediction;
				model->predict(face_resized,prediction,predicted_confidence);
				LOGD("Prediction = %d Predicted Confidence = %f",prediction,predicted_confidence);
				if(prediction>=0) {
					return prediction;
				}
			}
			return -1;
		}

	} catch(cv::Exception e)
	{
		LOGD("Find::cv::Exception: %s", e.what());
		jclass je = env->FindClass("org/opencv/core/CvException");
		if(!je)
		je = env->FindClass("java/lang/Exception");
		env->ThrowNew(je, e.what());
	}
	catch (...)
	{
		LOGD("Exceção não conhecida.");
		jclass je = env->FindClass("java/lang/Exception");
		env->ThrowNew(je, "Unknown exception in JNI code {highgui::VideoCapture_n_1VideoCapture__()}");
	}

return 0;
}

JNIEXPORT jstring JNICALL
Java_br_com_wsilva_opencvfacerecognition_util_FaceRecognition_stringFromJNI(JNIEnv *env, jobject /* this */) {
    std::string hello = "Hello from Face Recognition Native Library.";
    return env->NewStringUTF(hello.c_str());
 }
}
