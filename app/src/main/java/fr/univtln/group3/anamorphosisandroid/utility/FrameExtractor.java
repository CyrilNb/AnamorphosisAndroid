package fr.univtln.group3.anamorphosisandroid.utility;

import android.graphics.Bitmap;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;

import static junit.framework.Assert.fail;

/**
 * Recupère les frames d'une video
 */

public class FrameExtractor {

    // Principal
    CodecOutputSurface outputSurface;
    MediaExtractor extractor;
    MediaFormat format;
    MediaCodec decoder;

    // Parameter
    private String videoPath;

    // Log
    private static final String TAG = "ExtractMpegFrames";
    private static final boolean VERBOSE = false;


    // Conditions for extract
    boolean outputDone = false;
    boolean inputDone = false;

    // Debug
    int decodeCount = 0;
    long frameSaveTime = 0;

    public boolean isOutputDone() {
        return outputDone;
    }

    public int getWidth() {
        return format.getInteger(MediaFormat.KEY_WIDTH);
    }

    public int getHeight() {
        return format.getInteger(MediaFormat.KEY_HEIGHT);
    }

    public int getNbFrames() {
        System.out.println("duration: " + format.getLong(MediaFormat.KEY_DURATION));
        System.out.println("fps: " + format.getInteger(MediaFormat.KEY_FRAME_RATE));
        return (int) ((format.getLong(MediaFormat.KEY_DURATION) * format.getInteger(MediaFormat.KEY_FRAME_RATE)) / 1000000);
    }

    /**
     * Initialise les variables
     * @param videoPath
     */
    public FrameExtractor(String videoPath) {
        try {
            // Save videoPath
            this.videoPath = videoPath;

            // Select trackIndex
            File inputFile;
            inputFile = new File(videoPath);

            if (!inputFile.canRead()) {
                throw new FileNotFoundException("Unable to read " + inputFile);
            }
            extractor = new MediaExtractor();
            extractor.setDataSource(inputFile.toString());
            int trackIndex = selectTrack(extractor);
            if (trackIndex < 0) {
                throw new RuntimeException("No video track found in " + inputFile);
            }
            extractor.selectTrack(trackIndex);

            // Get format of the trackIndex
            format = extractor.getTrackFormat(trackIndex);
            if (VERBOSE) {
                Log.d(TAG, "Video size is " + format.getInteger(MediaFormat.KEY_WIDTH) + "x" +
                        format.getInteger(MediaFormat.KEY_HEIGHT));
                Log.d(TAG, "Video size is " + format.getInteger(MediaFormat.KEY_WIDTH) + "x" +
                        format.getInteger(MediaFormat.KEY_HEIGHT));
            }

            // Create outputSurface
            int width = format.getInteger(MediaFormat.KEY_WIDTH);
            int height = format.getInteger(MediaFormat.KEY_HEIGHT);
            outputSurface = new CodecOutputSurface(width, height);

            // Create MediaCodec decoder
            String mime = format.getString(MediaFormat.KEY_MIME);
            decoder = MediaCodec.createDecoderByType(mime);
            decoder.configure(format, outputSurface.getSurface(), null, 0);
            decoder.start();


        } catch (IOException e) {
            // release everything we grabbed
            if (outputSurface != null) {
                outputSurface.release();
                outputSurface = null;
            }
            if (decoder != null) {
                decoder.stop();
                decoder.release();
                decoder = null;
            }
            if (extractor != null) {
                extractor.release();
                extractor = null;
            }
            e.printStackTrace();
        }
        if (VERBOSE) Log.d(TAG, "FrameExtractor configured");
    }

    /**
     * Extraie une frame et la renvoie sous forme de Bitmap
     * @return
     */
    public Bitmap getNextBitmap() {

        final int TIMEOUT_USEC = 10000;
        int inputChunk = 0;
        ByteBuffer[] decoderInputBuffers = decoder.getInputBuffers();
        int trackIndex = extractor.getSampleTrackIndex();
        MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();

        Bitmap bitmap = null;

        // ---- INPUT ----
        if (!inputDone) {
            int inputBufIndex = decoder.dequeueInputBuffer(TIMEOUT_USEC);
            if (inputBufIndex >= 0) {
                ByteBuffer inputBuf = decoderInputBuffers[inputBufIndex];
                // Read the sample data into the ByteBuffer.  This neither respects nor
                // updates inputBuf's position, limit, etc.
                int chunkSize = extractor.readSampleData(inputBuf, 0);
                if (chunkSize < 0) {
                    // End of stream -- send empty frame with EOS flag set.
                    decoder.queueInputBuffer(inputBufIndex, 0, 0, 0L,
                            MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                    inputDone = true;
                    if (VERBOSE) Log.d(TAG, "sent input EOS");
                } else {
                    if (extractor.getSampleTrackIndex() != trackIndex) {
                        Log.w(TAG, "WEIRD: got sample from track " +
                                extractor.getSampleTrackIndex() + ", expected " + trackIndex);
                    }
                    long presentationTimeUs = extractor.getSampleTime();
                    decoder.queueInputBuffer(inputBufIndex, 0, chunkSize,
                            presentationTimeUs, 0 /*flags*/);
                    if (VERBOSE) {
                        Log.d(TAG, "submitted frame " + inputChunk + " to dec, size=" +
                                chunkSize);
                    }
                    inputChunk++;
                    extractor.advance();
                }
            } else {
                if (VERBOSE) Log.d(TAG, "input buffer not available");
            }
        }


        // ---- OUTPUT ----
        if (!outputDone) {
            int decoderStatus = decoder.dequeueOutputBuffer(info, TIMEOUT_USEC);
            if (decoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
                // no output available yet
                if (VERBOSE) Log.d(TAG, "no output from decoder available");
            } else if (decoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                // not important for us, since we're using Surface
                if (VERBOSE) Log.d(TAG, "decoder output buffers changed");
            } else if (decoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                MediaFormat newFormat = decoder.getOutputFormat();
                if (VERBOSE) Log.d(TAG, "decoder output format changed: " + newFormat);
            } else if (decoderStatus < 0) {
                fail("unexpected result from decoder.dequeueOutputBuffer: " + decoderStatus);
            } else { // decoderStatus >= 0
                if (VERBOSE) Log.d(TAG, "surface decoder given buffer " + decoderStatus +
                        " (size=" + info.size + ")");
                if ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                    if (VERBOSE) Log.d(TAG, "output EOS");
                    outputDone = true;
                }

                boolean doRender = (info.size != 0);

                // As soon as we call releaseOutputBuffer, the buffer will be forwarded
                // to SurfaceTexture to convert to a texture.  The API doesn't guarantee
                // that the texture will be available before the call returns, so we
                // need to wait for the onFrameAvailable callback to fire.
                decoder.releaseOutputBuffer(decoderStatus, doRender);
                if (doRender) {
                    if (VERBOSE) Log.d(TAG, "awaiting decode of frame " + decodeCount);
                    outputSurface.awaitNewImage();
                    outputSurface.drawImage(true);

                    long startWhen = System.nanoTime();
                    bitmap = outputSurface.displayFrame();
                    if (VERBOSE) Log.d(TAG, "decodeCount: " + decodeCount);
                    frameSaveTime += System.nanoTime() - startWhen;
                    decodeCount++;
                }
            }
        }
        return bitmap;
    }

    /**
     * Selects the video track, if any.
     *
     * @return the track index, or -1 if no video track is found.
     */
    public int selectTrack(MediaExtractor extractor) {
        // Select the first video track we find, ignore the rest.
        int numTracks = extractor.getTrackCount();
        for (int i = 0; i < numTracks; i++) {
            MediaFormat format = extractor.getTrackFormat(i);
            String mime = format.getString(MediaFormat.KEY_MIME);
            if (mime.startsWith("video/")) {
                if (VERBOSE) {
                    Log.d(TAG, "Extractor selected track " + i + " (" + mime + "): " + format);
                }
                return i;
            }
        }

        return -1;
    }

}
