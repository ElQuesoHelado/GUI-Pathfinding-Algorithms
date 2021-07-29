package GUIapplication;

public class DrawBufferShapes {
    private DrawBufferShapes() {
    }

    public static void drawSquare(double x, double y, int length, byte B, byte G, byte R, byte a, byte[] buffer,
                                  int bufferWidth) {
        int i;
        for (int yi = (int) y; yi < y + length - 1; ++yi) {
            for (int xi = (int) x; xi < x + length - 1; ++xi) {
                i = yi * bufferWidth + xi * 4;
                buffer[i] = B;
                buffer[i + 1] = G;
                buffer[i + 2] = R;
                buffer[i + 3] = a;
            }
        }
    }

    public static void drawLine(double x1, double y1, double x2, double y2, int width, byte B, byte G, byte R, byte a,
                                byte[] buffer, int bufferWidth) {
        int x = (int) x1, y = (int) y1, addX = (x1 < x2) ? 1 : -1,
                addY = (y1 < y2) ? 1 : -1, i;

        while (x != (int) x2 || y != (int) y2) {
            for (int xi = x - width / 2; xi <= x + width / 2; ++xi) {
                i = y * bufferWidth + xi * 4;
                buffer[i] = B;
                buffer[i + 1] = G;
                buffer[i + 2] = R;
                buffer[i + 3] = a;
            }

            if (x != (int) x2)
                x += addX;
            if (y != (int) y2)
                y += addY;

        }
    }
}
