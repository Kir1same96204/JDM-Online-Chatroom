package View.Utils;

import java.awt.*;
import javax.swing.border.Border;

public class RoundBorder implements Border {
    Color color = Color.BLACK;
    
	public Insets getBorderInsets(Component c) {
		return new Insets(0, 0, 0, 0);
	}
 
	public boolean isBorderOpaque() {
		return false;
	}
 
	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width,
			int height) {
		g.setColor(color);
		g.drawRoundRect(0, 0, c.getWidth() - 1, c.getHeight() - 1, 30, 30);
	}
}

