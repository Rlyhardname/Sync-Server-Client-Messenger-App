package Main;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;

public class ComponentListenerCustom implements MouseListener {

	ClientOperationGUI gui;

	public ComponentListenerCustom(ClientOperationGUI clientOperationGUI) {
		gui = clientOperationGUI;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		JLabel obj = (JLabel) e.getComponent();
		
		if (obj.equals(gui.friendOne)) {
			gui.frame.setTitle("account1(room ID: 1)");
		} else if ( obj.equals(gui.friendTwo)) {
			gui.frame.setTitle("account2(room ID: 2)");
		} else if ( obj.equals(gui.friendThree)) {
			gui.frame.setTitle("account3(room ID: 3)");
		} else if (obj.equals(gui.friendFour)) {
			gui.frame.setTitle("account4(room ID: 4)");
		} else if (obj.equals(gui.friendFive)) {
			gui.frame.setTitle("account5(room ID: 5)");
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

}
