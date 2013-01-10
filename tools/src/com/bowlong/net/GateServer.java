package com.bowlong.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import com.bowlong.util.Ref;

public class GateServer {

	public static void main(String[] args) throws IOException {
		if (args.length != 4) {
			System.out.println("java -jar gate.jar [args: listenPort host2 port2 skipN]");
			return;
		}

		int port = Integer.parseInt(args[0]);
		String host2 = args[1];
		int port2 = Integer.parseInt(args[2]);
		int skipN = Integer.parseInt(args[3]);

		// int port = 8888;
		// String host2 = "127.0.0.1";
		// int port2 = 3306;

		start(port, host2, port2, skipN);
	}

	public static void start(final int port, final String host2,
			final int port2, final int skipN) throws IOException {
		System.out.println("TCP:" + port + " host2:" + host2 + " port2:"
				+ port2);
		ServerSocket ss = new ServerSocket(port);
		try {

			while (true) {
				final Socket socket1 = ss.accept();
				new Thread(new Runnable() {
					@Override
					public void run() {
						final Ref<Boolean> isWorking = new Ref<Boolean>(true);
						byte[] buff1 = new byte[1024];

						try {
							final InputStream in1 = socket1.getInputStream();
							final OutputStream out1 = socket1.getOutputStream();
							if (skipN > 0)
								in1.skip(skipN);

							final Socket socket2 = new Socket(host2, port2);
							final InputStream in2 = socket2.getInputStream();
							final OutputStream out2 = socket2.getOutputStream();

							new Thread(new Runnable() {
								@Override
								public void run() {
									byte[] buff2 = new byte[1024];

									try {
										while (isWorking.val) {
											int len2 = in2.read(buff2);
											if (len2 < 0)
												throw new Exception("close");
											out1.write(buff2, 0, len2);
										}
									} catch (Exception e) {
										isWorking.val = false;
										e.printStackTrace();
									}
								}
							}).start();

							while (isWorking.val) {
								int len = in1.read(buff1);
								if (len < 0)
									throw new Exception("close");

								out2.write(buff1, 0, len);
							}

							socket1.close();
							socket2.close();
						} catch (Exception e) {
							e.printStackTrace();
							isWorking.val = true;
						}
					}
				}).start();
			}
		} catch (Exception e2) {
			e2.printStackTrace();
		} finally {
			ss.close();
		}

	}

}
