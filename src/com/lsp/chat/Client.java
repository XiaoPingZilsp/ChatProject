package com.lsp.chat;

import java.awt.BorderLayout;  
import java.awt.Color;  
import java.awt.GridLayout;  
import java.awt.Toolkit;  
import java.awt.event.ActionEvent;  
import java.awt.event.ActionListener;  
import java.awt.event.WindowAdapter;  
import java.awt.event.WindowEvent;  
import java.io.BufferedReader;  
import java.io.IOException;  
import java.io.InputStreamReader;  
import java.io.PrintWriter;  
import java.net.Socket;  
import java.util.HashMap;  
import java.util.Map;  
import java.util.StringTokenizer;  
  
import javax.swing.DefaultListModel;  
import javax.swing.ImageIcon;
import javax.swing.JButton;  
import javax.swing.JFrame;  
import javax.swing.JLabel;  
import javax.swing.JList;  
import javax.swing.JOptionPane;  
import javax.swing.JPanel;  
import javax.swing.JScrollPane;  
import javax.swing.JSplitPane;  
import javax.swing.JTextArea;  
import javax.swing.JTextField;  
import javax.swing.border.TitledBorder;  
  
public class Client{  
  
    private JFrame frame;  
    private JList userList;  
    private JTextArea textArea;  
    private JTextField textField;   
    private JLabel lb_port; 
    private JLabel lb_hostIp;  
    private JLabel lb_name;
    private JButton btn_start;  
    private JButton btn_stop;  
    private JButton btn_send;
    private JButton btn_all;
    private JButton btn_one;
    private JPanel northPanel;  
    private JPanel southPanel;  
    private JScrollPane rightScroll;  
    private JScrollPane leftScroll;  
    private JSplitPane centerSplit; 
  
  
    private DefaultListModel listModel;  
    private boolean isConnected = false;  
  
    private Socket socket;  
    private PrintWriter writer;  
    private BufferedReader reader;  
    private MessageThread messageThread;// ���������Ϣ���߳�  
    private Map<String, User> onLineUsers = new HashMap<String, User>();// ���������û�  
  
    // ������,�������  
    public static void main(String[] args) {  
        new Client();  
    }  
  
    // ִ�з���  
    public void send() { 
    	String message = textField.getText(); 
    	if(userList.isSelectionEmpty()){
    		String message1=null;
    		sendMessage(frame.getTitle() + "@" + "ALL" + "@" + message+"@"+message1);  
    		textField.setText(null);  
        }else{
        	String message1 = (String) userList.getSelectedValue(); 
        	textArea.append(frame.getTitle()+":"+message+"\r\n");
            sendMessage(frame.getTitle() + "@" + "ONE" + "@"+message+"@"+message1);
            textField.setText(null); 
        }
    }  
  
    // ���췽��  
    public Client() {  
    	// ��Ա����
        textArea = new JTextArea();  
        textArea.setEditable(false);   
        textField = new JTextField();  
        lb_port = new JLabel("8080");  
        lb_hostIp = new JLabel("127.0.0.1");  
        lb_name = new JLabel("Сming");  
        btn_start = new JButton("����");  
        btn_stop = new JButton("�Ͽ�");  
        btn_send = new JButton("����");  
        btn_all = new JButton("Ⱥ��");
        btn_one = new JButton("˽��");
        listModel = new DefaultListModel();  
        userList = new JList(listModel);  
        
        //����
        northPanel = new JPanel();  
        northPanel.setLayout(new GridLayout(1, 7));  
         
        northPanel.add(new JLabel("�û�������"));  
        northPanel.add(lb_name);  
        northPanel.add(btn_all); 
        northPanel.add(btn_one);
        northPanel.add(btn_start);  
        northPanel.add(btn_stop);  
        northPanel.setBorder(new TitledBorder("����״̬��"));  
  
        rightScroll = new JScrollPane(textArea);  
        rightScroll.setBorder(new TitledBorder("��Ϣ��ʾ��"));  
        leftScroll = new JScrollPane(userList);  
        leftScroll.setBorder(new TitledBorder("�����û���"));  
        southPanel = new JPanel(new BorderLayout());  
        southPanel.add(textField, "Center");  
        southPanel.add(btn_send, "East");  
        southPanel.setBorder(new TitledBorder("��д��Ϣ��"));  
  
        centerSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftScroll,  
                rightScroll);  
        centerSplit.setDividerLocation(100);  
  
        frame = new JFrame("�ͻ���");    
        frame.setLayout(new BorderLayout());  
        frame.add(northPanel, "North");  
        frame.add(centerSplit, "Center");  
        frame.add(southPanel, "South");  
        frame.setSize(600, 400);  
        int screen_width = Toolkit.getDefaultToolkit().getScreenSize().width;  
        int screen_height = Toolkit.getDefaultToolkit().getScreenSize().height;  
        frame.setLocation((screen_width - frame.getWidth()) / 2,  
                (screen_height - frame.getHeight()) / 2);  
        frame.setVisible(true);  
  
        // д��Ϣ���ı����а��س���ʱ�¼�  
        textField.addActionListener(new ActionListener() {  
            public void actionPerformed(ActionEvent arg0) {  
                send();  
            }  
        });  
  
        // �������Ͱ�ťʱ�¼�  
        btn_send.addActionListener(new ActionListener() {  
            public void actionPerformed(ActionEvent e) {  
                send();  
            }  
        });  
  
        // �������Ӱ�ťʱ�¼�  
        btn_start.addActionListener(new ActionListener() {  
            public void actionPerformed(ActionEvent e) {  
                int port;  
                port = Integer.parseInt(lb_port.getText());
                String hostIp = lb_hostIp.getText();  
                String name = lb_name.getText();  
                connectServer(port, hostIp, name);
                frame.setTitle(name); 
               
            }  
        });  
  
        // �����Ͽ���ťʱ�¼�  
        btn_stop.addActionListener(new ActionListener() {  
            public void actionPerformed(ActionEvent e) {  
            	
            	closeConnection();// �Ͽ�����  
            	textArea.setText("");
            }  
        });  
        
        //Ⱥ��
        btn_all.addActionListener(new ActionListener() {  
            public void actionPerformed(ActionEvent e) {  
            	userList.clearSelection();
            	textArea.setText("");
            	textArea.append("Ⱥ����..."+"\r\n");
            }  
        });  
       // ˽��
        btn_one.addActionListener(new ActionListener() {  
            public void actionPerformed(ActionEvent e) {  
            	textArea.setText("");
            	textArea.append(" ˽����..."+"\r\n");
            }  
        });  
        // �رմ���ʱ�¼�  
        frame.addWindowListener(new WindowAdapter() {  
            public void windowClosing(WindowEvent e) {  
                if (isConnected) {  
                    closeConnection();// �ر�����  
                }  
                System.exit(0);// �˳�����  
            }  
        });  
    }  
  
    
     // ���ӷ�����  
    public boolean connectServer(int port, String hostIp, String name) {  
        
        try {  
            socket = new Socket(hostIp, port);// ���ݶ˿ںźͷ�����ip��������  
            writer = new PrintWriter(socket.getOutputStream());  
            reader = new BufferedReader(new InputStreamReader(socket  
                    .getInputStream()));  
            // ���Ϳͻ����û�������Ϣ(�û�����ip��ַ)  
            sendMessage(name + "@" + socket.getLocalAddress().toString());  
            // ����������Ϣ���߳�  
            messageThread = new MessageThread(reader, textArea);  
            messageThread.start();  
            isConnected = true;// �Ѿ��������� 
            return true;  
        } catch (Exception e) {  
            textArea.append("��˿ں�Ϊ��" + port + "    IP��ַΪ��" + hostIp  
                    + "   �ķ���������ʧ��!" + "\r\n");  
            isConnected = false;// δ������  
            return false;  
        }  
    }  
  
    // ������Ϣ 
    public void sendMessage(String message) {  
        writer.println(message);  
        writer.flush();  
    }  
  
   
     
     
    // �ͻ��������ر�����
    public synchronized boolean closeConnection() {  
        try {  
            sendMessage("CLOSE");// ���ͶϿ����������������  
            messageThread.stop();// ֹͣ������Ϣ�߳�  
            // �ͷ���Դ  
            if (reader != null) {  
                reader.close();  
            }  
            if (writer != null) {  
                writer.close();  
            }  
            if (socket != null) {  
                socket.close();  
            }  
            isConnected = false;  
            return true;  
        } catch (IOException e1) {  
            e1.printStackTrace();  
            isConnected = true;  
            return false;  
        }  
    }  
    
 
    // ���Ͻ�����Ϣ���߳�  
    class MessageThread extends Thread {  
        private BufferedReader reader;  
        private JTextArea textArea;  
  
        // ������Ϣ�̵߳Ĺ��췽��  
        public MessageThread(BufferedReader reader, JTextArea textArea) {  
            this.reader = reader;  
            this.textArea = textArea;  
        }  
  
        // �����Ĺر�����  
        public synchronized void closeCon() throws Exception {  
            // ����û��б�  
            listModel.removeAllElements();  
            // �����Ĺر������ͷ���Դ  
            if (reader != null) {  
                reader.close();  
            }  
            if (writer != null) {  
                writer.close();  
            }  
            if (socket != null) {  
                socket.close();  
            }  
            isConnected = false;// �޸�״̬Ϊ�Ͽ�  
        }  
  
        public void run() {  
            String message = "";  
            while (true) {  
                try {  
                    message = reader.readLine();  
                    StringTokenizer stringTokenizer = new StringTokenizer(  
                            message, "/@");  
                    String command = stringTokenizer.nextToken();// ����  
                    if (command.equals("CLOSE"))// �������ѹر�����  
                    {  
                        textArea.append("�������ѹر�!\r\n");  
                        closeCon();// �����Ĺر�����  
                        return;// �����߳�  
                    } else if (command.equals("ADD")) {// ���û����߸��������б�  
                        String username = "";  
                        String userIp = "";  
                        if ((username = stringTokenizer.nextToken()) != null  
                                && (userIp = stringTokenizer.nextToken()) != null) {  
                            User user = new User(username, userIp);  
                            onLineUsers.put(username, user);  
                            listModel.addElement(username);  
                        }  
                    } else if (command.equals("DELETE")) {// ���û����߸��������б�  
                        String username = stringTokenizer.nextToken();  
                        User user = (User) onLineUsers.get(username);  
                        onLineUsers.remove(user);  
                        listModel.removeElement(username);  
                    } else if (command.equals("USERLIST")) {// ���������û��б�  
                        int size = Integer  
                                .parseInt(stringTokenizer.nextToken());  
                        String username = null;  
                        String userIp = null; 
                       
                        for (int i = 0; i < size; i++) {  
                            username = stringTokenizer.nextToken();  
                            userIp = stringTokenizer.nextToken();  
                            User user = new User(username, userIp);  
                            onLineUsers.put(username, user);  
                            listModel.addElement(username);  
                        }  
                    }  else {// ��ͨ��Ϣ  
                        textArea.append(message + "\r\n");  
                    }  
                } catch (IOException e) {  
                    e.printStackTrace();  
                } catch (Exception e) {  
                    e.printStackTrace();  
                }  
            }  
        }  
    }  
}  

