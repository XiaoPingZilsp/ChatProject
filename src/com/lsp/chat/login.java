package com.lsp.chat;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class login {

	public static void main(String[] args) {
		JTextField txt_ip;
		JTextField txt_name;
		JButton btn_login;
		JPanel northPanel;
		JPanel centerPanel;
		JPanel southPanel;
		
		
		JFrame frame=new JFrame();
		txt_ip=new JTextField();
		txt_name=new JTextField();
		btn_login=new JButton("µÇÂ¼ ");
		
		//frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(260, 180);
		//Container c=frame.getContentPane();
		frame.setLayout(new BorderLayout());
		
		northPanel = new JPanel();
		northPanel.add(new JLabel("ip£º"));  
	    northPanel.add(txt_ip);  
	    
	    centerPanel = new JPanel();
	    centerPanel.add(new JLabel("ÓÃ»§Ãû£º"));  
	    centerPanel.add(txt_name); 
	    
	    southPanel = new JPanel();
	    southPanel.add(btn_login);  
	     
		frame.add(northPanel,"North");
		frame.add(centerPanel,"Center");
		frame.add (southPanel,"South");
		frame.setVisible(true);
		
	}

}
