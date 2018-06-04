package fr.univtln.group3.anamorphosisandroid;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;

import java.util.Random;


/**
 * Cette classe contient différents algos de traitement d'image
 * dont certains servent pour l'anti-aliasing
 */
public class ConvolutionFilter
{

    /**
     * Les couleurs restent dans l'intervalle de 0 à 255 (8 bits)
     *
     * @param color
     * @return color, la couleur qui a été estimé
     * - 0 si la valeur de la couleur de base est inférieur à 0 ou
     * - 255 si elle est supérieur à 255
     */
    private static int reduceRangeColor(int color) {
        if (color < 0) {
            return 0;
        }
        if (color > 255) {
            return 255;
        }
        return color;
    }

    public static class ConvoFilterMethod {
        /**
         * Taille de la Matrice de convolution (Ici on travaille avec des matrices 3x3)
         */
        private static final int MATRIX_SIZE = 3;

        /**
         * Kernel avec matrice de convolution 3x3
         *
         * @param bmp
         * @param mat
         * @param factor
         * @param offset
         * @return bmp, la Bitmap mise en paramètre qui a été modifié
         * avec l'application d'un filtre (Matrice de convolution 3x3, un facteur et offset)
         */
        private static Bitmap convolution3x3(Bitmap bmp, Matrix mat, float factor, int offset) {

            float[] matrixV = new float[MATRIX_SIZE * MATRIX_SIZE];
            mat.getValues(matrixV);


            int width = bmp.getWidth();
            int height = bmp.getHeight();
            int[] pxlsSrc = new int[width * height];
            bmp.getPixels(pxlsSrc, 0, width, 0, 0, width, height);


            int[] pxlsFinal = pxlsSrc.clone();
            int r, g, b;
            int rSum, gSum, bSum;
            int idx;
            int pix;
            float matValue;
            for (int x = 0, w = width - MATRIX_SIZE + 1; x < w; ++x) {
                for (int y = 0, h = height - MATRIX_SIZE + 1; y < h; ++y) {
                    idx = (x + 1) + (y + 1) * width;
                    rSum = gSum = bSum = 0;
                    for (int itMatX = 0; itMatX < MATRIX_SIZE; ++itMatX) {
                        for (int itMatY = 0; itMatY < MATRIX_SIZE; ++itMatY) {
                            pix = pxlsSrc[(x + itMatX) + (y + itMatY) * width];
                            matValue = matrixV[itMatX + itMatY * MATRIX_SIZE];
                            rSum += (Color.red(pix) * matValue);
                            gSum += (Color.green(pix) * matValue);
                            bSum += (Color.blue(pix) * matValue);
                        }
                    }
                    r = reduceRangeColor((int) (rSum / factor + offset));
                    g = reduceRangeColor((int) (gSum / factor + offset));
                    b = reduceRangeColor((int) (bSum / factor + offset));


                    pxlsFinal[idx] = Color.argb(Color.alpha(pxlsSrc[idx]), r, g, b);
                }
            }

            bmp.setPixels(pxlsFinal, 0, width, 0, 0, width, height);

            return bmp;
        }

        /** Les méthodes utilisant le kernel avec matrice de convolution 3x3 **/

        /**
         * Flou gaussien
         *
         * @param source
         * @return source, la Bitmap source qui a été modifié avec l'application d'un filtre "gaussian blur"
         */
        public static Bitmap gaussianBlurFilter(Bitmap source) {

            Matrix gaussianBlur = new Matrix();
            gaussianBlur.setValues(new float[]{
                    1, 2, 1,
                    2, 4, 2,
                    1, 2, 1
            });

            source = convolution3x3(source, gaussianBlur, 16, 0);

            return source;

        }

        /**
         * Accentuation
         *
         * @param source
         * @return source, la Bitmap source qui a été modifié avec l'application d'un filtre "sharpen"
         */
        public static Bitmap sharpenFilter(Bitmap source) {

            Matrix sharpen = new Matrix();
            sharpen.setValues(new float[]{
                    0, -2, 0,
                    -2, 11, -2,
                    0, -2, 0
            });

            source = convolution3x3(source, sharpen, 3, 0);

            return source;
        }


        /**
         * Recherche de formes (avec accentuation)
         *
         * @param source
         * @return source, la Bitmap source qui a été modifié avec l'application d'un filtre "shape searching + sharpen"
         */
        public static Bitmap sharpenSearchingFilter(Bitmap source) {

            Matrix sharpenSearching = new Matrix();
            sharpenSearching.setValues(new float[]{
                    1, 0, 0,
                    1, 0, 0,
                    1, 1, 0

            });

            source = convolution3x3(source, sharpenSearching, 3, 1);

            return source;

        }

        /**
         * "Gravure"
         *
         * @param source
         * @return source, la Bitmap source qui a été modifié avec l'application d'un filtre "engraved"
         */
        public static Bitmap engraveFilter(Bitmap source) {

            Matrix engrave = new Matrix();
            engrave.setValues(new float[]{
                    -2, 0, 0,
                    0, 2, 0,
                    0, 0, 1
            });

            source = convolution3x3(source, engrave, 1, 95);

            return source;
        }

        /**
         * Lissage
         *
         * @param source
         * @return source, la Bitmap source qui a été modifié avec l'application d'un filtre "smooth"
         */
        public static Bitmap smoothFilter(Bitmap source) {

            Matrix smooth = new Matrix();
            smooth.setValues(new float[]{
                    1, 1, 1,
                    1, 5, 1,
                    1, 1, 1
            });


            source = convolution3x3(source, smooth, 13, 1);

            return source;
        }

        /**
         * Rééquilibrage par moyenne de couleurs
         *
         * @param source
         * @return source, la Bitmap source qui a été modifié avec l'application d'un filtre "mean removal"
         */
        public static Bitmap meanRemovalFilter(Bitmap source) {

            Matrix meanRemoval = new Matrix();
            meanRemoval.setValues(new float[]{
                    -1, -1, -1,
                    -1, 9, -1,
                    -1, -1, -1
            });


            source = convolution3x3(source, meanRemoval, 1, 0);

            return source;
        }

    }

    /**
     * Les méthodes de la classe suivante n'utilisent pas le système avec matrice de convolution.
     * On ne peut pas les utiliser avant un ConvoFilterMethod,
     * mais on peut les utiliser après avoir appliquer un ConvoFilterMethod.
     * Pour résumer, les NonConvoFilterMethod s'appliquent:
     *      - soit seuls;
     *      - soit après un ConvoFilterMethod et donc
     *          la NonConvoFilterMethod doit être la dernière méthode à être utilisé.
     **/
    public static class NonConvoFilterMethod {

        //public static final int COLOR_MIN = 0x00;
        private static final int COLOR_MAX = 0xFF;

        /**
         * Les valeurs restent dans l'intervalle 0 à max (la valeur max)
         *
         * @param value
         * @param max
         * @return value, la valeur qui prend
         * - 0 si la valeur de base est inférieur à 0 ou
         * - (max - 1) si elle est supérieur à la valeur max de l'intervalle
         */
        private static int wrapRangeValue(int value, int max) {
            if (value < 0) {
                return 0;
            }
            if (value >= max) {
                return max - 1;
            }

            return value;
        }

        /**
         * Attenuation des couleurs
         *
         * @param source
         * @param bitOffset
         * @return source, la Bitmap source qui a été modifié avec l'application d'un filtre "color decreasing"
         */
        public static Bitmap decreaseColorDepth(Bitmap source, int bitOffset) {

            int width = source.getWidth();
            int height = source.getHeight();

            int[] pixels = new int[height * width];

            source.getPixels(pixels, 0, width, 0, 0, width, height);

            int R, G, B;

            int index = 0;

            for (int x = 0; x < width; ++x) {
                for (int y = 0; y < height; ++y) {

                    index = y * width + x;

                    R = Color.red(pixels[index]);
                    G = Color.green(pixels[index]);
                    B = Color.blue(pixels[index]);


                    R = ((R + (bitOffset / 2)) - ((R + (bitOffset / 2)) % bitOffset) - 1);
                    if (R < 0) {
                        R = 0;
                    }
                    G = ((G + (bitOffset / 2)) - ((G + (bitOffset / 2)) % bitOffset) - 1);
                    if (G < 0) {
                        G = 0;
                    }
                    B = ((B + (bitOffset / 2)) - ((B + (bitOffset / 2)) % bitOffset) - 1);
                    if (B < 0) {
                        B = 0;
                    }

                    pixels[index] = Color.argb(Color.alpha(pixels[index]), R, G, B);
                }
            }

            source.setPixels(pixels, 0, width, 0, 0, width, height);

            return source;
        }

        /**
         * "Bruit"
         *
         * @param source
         * @return source, la Bitmap source qui a été modifié avec l'application d'un filtre "flea effect" ou "noise"
         */

        public static Bitmap applyFleaEffect(Bitmap source) {

            int width = source.getWidth();
            int height = source.getHeight();
            int[] pixels = new int[width * height];

            source.getPixels(pixels, 0, width, 0, 0, width, height);

            Random random = new Random();

            int index = 0;

            for (int y = 0; y < height; ++y) {
                for (int x = 0; x < width; ++x) {

                    index = y * width + x;

                    int randColor = Color.rgb(random.nextInt(COLOR_MAX),
                            random.nextInt(COLOR_MAX), random.nextInt(COLOR_MAX));

                    pixels[index] |= randColor;
                }
            }

            Bitmap bmOut = Bitmap.createBitmap(width, height, source.getConfig());
            bmOut.setPixels(pixels, 0, width, 0, 0, width, height);
            return bmOut;
        }


        /**
         * Remplacement de couleur
         *
         * @param source
         * @param fromColor
         * @param targetColor
         * @return source, la Bitmap source qui a été modifié avec l'application d'un filtre "replace color"
         */
        public static Bitmap replaceColor(Bitmap source, int fromColor, int targetColor) {

            int width = source.getWidth();
            int height = source.getHeight();
            int[] pixels = new int[width * height];
            source.getPixels(pixels, 0, width, 0, 0, width, height);

            for (int x = 0; x < pixels.length; ++x) {
                if (pixels[x] == Color.TRANSPARENT) {
                    System.out.println("Transparent : " + pixels[x]);
                }
                pixels[x] = (pixels[x] == fromColor) ? targetColor : pixels[x];

            }

            source.setPixels(pixels, 0, width, 0, 0, width, height);

            return source;
        }


        /**
         * Flou de mouvement gaussien approximatif
         *
         * @param input
         * @param radius
         * @return source, la Bitmap source qui a été modifié avec l'application d'un filtre "approximative motion gaussian blur"
         */
        public static Bitmap approxGaussianBlur(Bitmap input, int radius) {

            Bitmap temp = null;

            for (int i = 0; i < 3; i++) {
                if (temp == null) {
                    temp = boxHandVBlur(input, radius);
                } else {
                    temp = boxHandVBlur(temp, radius);
                }
            }
            return temp;
        }


        /**
         * Flou de mouvement gaussien vertical puis horizontal
         *
         * @param source
         * @param radius
         * @return source, la Bitmap source qui a été modifié
         * avec l'application d'un filtre "motion gaussian blur Vertical then Horizontal"
         */
        private static Bitmap boxHandVBlur(Bitmap source, int radius) {
            return horizontalGB(verticalGB(source, radius), radius);
        }


        /**
         * Flou de mouvement gaussien horizontal puis vertical
         *
         * @param source
         * @param radius
         * @return source, la Bitmap source qui a été modifié
         * avec l'application d'un filtre "motion gaussian blur Horizontal then Vertical"
         */
        private static Bitmap boxVandHBlur(Bitmap source, int radius) {
            return verticalGB(horizontalGB(source, radius), radius);
        }


        /**
         * Flou de mouvement gaussien horizontal
         *
         * @param input
         * @param radius
         * @return source, la Bitmap source qui a été modifié
         * avec l'application d'un filtre "motion gaussian blur Horizontal"
         */
        private static Bitmap horizontalGB(Bitmap input, int radius) {

            int width = input.getWidth();
            int height = input.getHeight();

            int[] pixels = new int[width * height];
            input.getPixels(pixels, 0, width, 0, 0, width, height);

            int[] outPixels = new int[width * height];
            input.getPixels(outPixels, 0, width, 0, 0, width, height);

            for (int y = 0; y < height; y++) {
                int averageR = 0, averageG = 0, averageB = 0;

                for (int x = 0; x < width; x++) {
                    if (x == 0) {
                        for (int i = -radius; i <= radius; i++) {
                            int pixel = pixels[(y * width) + wrapRangeValue(x + i, width)];
                            int r = Color.red(pixel);
                            int g = Color.green(pixel);
                            int b = Color.blue(pixel);

                            averageR += r;
                            averageG += g;
                            averageB += b;
                        }
                    } else {
                        int currentRightMost = pixels[(y * width) + wrapRangeValue(x + radius, width)];
                        int previousLeftMost = pixels[(y * width) + wrapRangeValue((x - 1) - radius, width)];

                        int currentR = Color.red(currentRightMost);
                        int currentG = Color.green(currentRightMost);
                        int currentB = Color.blue(currentRightMost);

                        int prevR = Color.red(previousLeftMost);
                        int prevG = Color.green(previousLeftMost);
                        int prevB = Color.blue(previousLeftMost);

                        averageR -= prevR;
                        averageG -= prevG;
                        averageB -= prevB;

                        averageR += currentR;
                        averageG += currentG;
                        averageB += currentB;
                    }

                    int newColor = Color.argb(255, reduceRangeColor(averageR / (2 * radius + 1)), reduceRangeColor(averageG / (2 * radius + 1)), reduceRangeColor(averageB / (2 * radius + 1)));
                    outPixels[(y * width) + x] = newColor;
                }
            }

            return Bitmap.createBitmap(outPixels, width, height, input.getConfig());
        }


        /**
         * Flou de mouvement gaussien vertical
         *
         * @param input
         * @param radius
         * @return source, la Bitmap source qui a été modifié
         * avec l'application d'un filtre "motion gaussian blur Vertical
         */
        private static Bitmap verticalGB(Bitmap input, int radius) {
            int width = input.getWidth();
            int height = input.getHeight();
            int[] pixels = new int[width * height];
            input.getPixels(pixels, 0, width, 0, 0, width, height);

            int[] outPixels = new int[width * height];
            input.getPixels(outPixels, 0, width, 0, 0, width, height);

            for (int x = 0; x < width; x++) {

                int averageR = 0, averageG = 0, averageB = 0;

                for (int y = 0; y < height; y++) {

                    if (y == 0) {
                        for (int i = -radius; i <= radius; i++) {
                            int pixel = pixels[(wrapRangeValue(y + i, height) * width) + x];
                            float r = Color.red(pixel);
                            float g = Color.green(pixel);
                            float b = Color.blue(pixel);

                            averageR += r;
                            averageG += g;
                            averageB += b;
                        }
                    } else {
                        int currentRightMost = pixels[(wrapRangeValue(y + radius, height) * width) + x];
                        int previousLeftMost = pixels[(wrapRangeValue((y - 1) - radius, height) * width) + x];

                        int currentR = Color.red(currentRightMost);
                        int currentG = Color.green(currentRightMost);
                        int currentB = Color.blue(currentRightMost);

                        int prevR = Color.red(previousLeftMost);
                        int prevG = Color.green(previousLeftMost);
                        int prevB = Color.blue(previousLeftMost);

                        averageR -= prevR;
                        averageG -= prevG;
                        averageB -= prevB;

                        averageR += currentR;
                        averageG += currentG;
                        averageB += currentB;
                    }

                    int newColor = Color.argb(255, reduceRangeColor(averageR / (2 * radius + 1)), reduceRangeColor(averageG / (2 * radius + 1)), reduceRangeColor(averageB / (2 * radius + 1)));
                    outPixels[(y * width) + x] = newColor;
                }
            }

            return Bitmap.createBitmap(outPixels, width, height, input.getConfig());
        }


        /**
         * Calcule de la couleur moyenne de toute l'image
         *
         * @param source
         * @param nbJumpedPixels
         * @return la couleur moyenne de l'image selon le nombre de pixel a sauté (nbJumpedPixels)
         */
        public int calculateAverageColor(Bitmap source, int nbJumpedPixels) {
            int R = 0;
            int G = 0;
            int B = 0;
            int height = source.getHeight();
            int width = source.getWidth();
            int n = 0;
            int[] pixels = new int[width * height];
            source.getPixels(pixels, 0, width, 0, 0, width, height);
            for (int i = 0; i < pixels.length; i += nbJumpedPixels) {
                int color = pixels[i];
                R += Color.red(color);
                G += Color.green(color);
                B += Color.blue(color);
                n++;
            }
            return Color.rgb(R / n, G / n, B / n);
        }


    }
    //TODO à utiliser dans une méthode pour faire un filtre

    /*float[][] mrtl = new double[][]{
            { -1, -1, -1 },
            { -1,  8, -1 },
            { -1, -1, -1 }
    };

    float[][] reverseMrtl = new double[][]{
            { 1,  1 , 1 },
            { 1, -8 , 1 },
            { 1,  1 , 1 }
    };*/

}
