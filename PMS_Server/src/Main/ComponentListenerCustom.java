package Main;

import java.awt.Color;
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

		gui.changeLabelColor(gui.friendOne, Color.black);
		gui.changeLabelColor(gui.friendTwo, Color.black);
		gui.changeLabelColor(gui.friendThree, Color.black);
		gui.changeLabelColor(gui.friendFour, Color.black);
		gui.changeLabelColor(gui.friendFive, Color.black);
		gui.changeLabelColor(gui.fileDemonstrationRoom, Color.black);

		if (obj.equals(gui.friendOne)) {
			String[] friend = gui.friendOne.getText().split("\\s+");
			gui.changeLabelColor(gui.friendOne, Color.RED);
			gui.setRoom(Integer.parseInt(friend[0]));
		} else if (obj.equals(gui.friendTwo)) {
			String[] friend = gui.friendTwo.getText().split("\\s+");
			gui.changeLabelColor(gui.friendTwo, Color.RED);
			gui.setRoom(Integer.parseInt(friend[0]));
		} else if (obj.equals(gui.friendThree)) {
			String[] friend = gui.friendThree.getText().split("\\s+");
			gui.changeLabelColor(gui.friendThree, Color.RED);
			gui.setRoom(Integer.parseInt(friend[0]));
		} else if (obj.equals(gui.friendFour)) {
			String[] friend = gui.friendFour.getText().split("\\s+");
			gui.changeLabelColor(gui.friendFour, Color.RED);
			gui.setRoom(Integer.parseInt(friend[0]));
		} else if (obj.equals(gui.friendFive)) {
			String[] friend = gui.friendFive.getText().split("\\s+");
			gui.changeLabelColor(gui.friendFive, Color.RED);
			gui.setRoom(Integer.parseInt(friend[0]));
		} else if (obj.equals(gui.fileDemonstrationRoom)) {
			String[] friend = gui.fileDemonstrationRoom.getText().split("\\s+");
			gui.changeLabelColor(gui.fileDemonstrationRoom, Color.RED);
			gui.setRoom(Integer.parseInt(friend[0]));
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
