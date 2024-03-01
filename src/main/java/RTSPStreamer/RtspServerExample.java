package RTSPStreamer;
import org.freedesktop.gstreamer.Gst;
import org.freedesktop.gstreamer.Pipeline;
import org.freedesktop.gstreamer.rtsp.server.RTSPMediaFactory;
import org.freedesktop.gstreamer.rtsp.server.RTSPServer;
import org.freedesktop.gstreamer.rtsp.server.MountPoints;

public class RtspServerExample {

    public static void main(String[] args) {
        // Initialize GStreamer
        Gst.init("RtspServerExample");

        // Create a pipeline
        Pipeline pipeline = (Pipeline) Gst.parseLaunch("fdsrc ! queue ! h264parse ! rtph264pay config-interval=1 name=pay0 pt=96");

        // Create an RTSP server
        RTSPServer server = new RTSPServer();
        server.setService("8554"); // Port number

        // Create a factory for the media
        RTSPMediaFactory factory = new RTSPMediaFactory();
        factory.setLaunch("( " + pipeline.getName() + " )");
        factory.setShared(true);

        // Add the factory to the /test mount point of the RTSP server
        MountPoints mounts = server.getMountPoints();
        mounts.addFactory("/test", factory);

        // Start the server
        server.attach(null);

        System.out.println("RTSP stream ready at rtsp://<Your_Raspberry_Pi_IP>:8554/test");

        // Run the main loop
        Gst.main();
    }
}
