package com.driver;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Repository
public class WhatsappRepository {

    private HashMap<Group,List<User>> groupAndUserDb=new HashMap<>();//group and user list
    private HashMap<Group,List<Message>> groupAndMessageDb=new HashMap<>();//group and message
    private HashMap<Message, User> senderMap;
    private HashMap<Group, User> adminMap;
    //private HashSet<String> userMobile;
    private HashMap<String,User> mobileUserDb=new HashMap<>();//mobile ,no user
    private int messageId=0;
    private int customGroupCount;

    public WhatsappRepository() {
        this.groupAndMessageDb = new HashMap<Group, List<Message>>();
        this.groupAndUserDb = new HashMap<Group, List<User>>();
        this.senderMap = new HashMap<Message, User>();
        this.adminMap = new HashMap<Group, User>();
        this.mobileUserDb = new HashMap<>();
        this.customGroupCount = 0;
        this.messageId = 0;
    }

    public String createUSer(String name, String mobile) throws Exception {
        if(mobileUserDb.containsKey(mobile)) throw new Exception("User already exists");
        User user=new User(name,mobile);
        mobileUserDb.put(mobile,user);
        return "SUCCESS";
    }

    public Group createGroup(List<User> users) {
//        List<User> userList=users;
//        User first=userList.get(0);
//        String firstName= first.getName();
//        int totalNo=userList.size();
//        String name1="";
//        //Group group=new Group();
//        if(totalNo==2){
//           name1= userList.get(1).getName();
//        }
//        else{
//             name1="Group "+totalNo;
//
//        }
//        Group group=new Group(name1,totalNo);
//adminMap.put(group,first);////add admin to admin map
//        return group;

        List<User> listOfUser = users;
        User admin = listOfUser.get(0);
        String groupName = "";
        int numberOfParticipants = listOfUser.size();

        if(listOfUser.size() == 2){//the group is a personal chat
            groupName = listOfUser.get(1).getName();
        }
        else{
            this.customGroupCount += 1;
            groupName = "Group " + customGroupCount;
        }

        Group group = new Group(groupName,numberOfParticipants);
        adminMap.put(group,admin);// add admin to the adminMap
        groupAndUserDb.put(group,users);// add the list of group to the group-user-Map
        return  group;
    }

    public int createMessage(String content) {
        this.messageId += 1;
        Message message = new Message(messageId,content);
        return messageId;
    }

    public int sendMessage(Message message, User sender, Group group) throws Exception {
        if(adminMap.containsKey(group)){
            List<User> users = groupAndUserDb.get(group);
            Boolean userFound = false;
            for(User user: users){
                if(user.equals(sender)){
                    userFound = true;
                    break;
                }
            }
            if(userFound){
                senderMap.put(message, sender);
                if(groupAndUserDb.containsKey(group)){
                    if(groupAndMessageDb.get(group) !=null ){
                        List<Message> messages = groupAndMessageDb.get(group);// it was giving me null pointer exception

                        messages.add(message);
                        groupAndMessageDb.put(group, messages);
                        return messages.size();
                    }else{
                        List<Message> newMessage = new ArrayList<>();
                        newMessage.add(message);
                        groupAndMessageDb.put(group, newMessage);
                        return newMessage.size();
                    }

                }

            }
            throw new Exception("You are not allowed to send message");
        }
        throw new Exception("Group does not exist");
    }

    public String changeAdmin(User approver, User user, Group group) throws Exception {
        if(groupAndUserDb.containsKey(group)){
            if(adminMap.containsKey(group)){
                List<User> listOfUser = groupAndUserDb.get(group);
                if(listOfUser.contains(user)) {

                    adminMap.put(group,user);
                    return "SUCCESS";
                }
                throw new Exception("User is not a participant");
            }
            throw new Exception("Approver does not have rights");
        }
        throw new Exception("Group does not exist");
    }

    public int removeUser(User user) throws Exception {
        Group groupuser=new Group();
        for(Group group:groupAndUserDb.keySet()){
            List<User> userList=groupAndUserDb.get(group);
            for(User users:userList){
                if(users.equals(user)){
                    groupuser=group;
                    break;
                }
            }
        }
        if(groupuser.getName()==null){
            throw new Exception("User not found");
        }
        else if(adminMap.get(groupuser).equals(user)){
            throw new Exception("Cannot remove admin");
        }
        else{ List<User> users=groupAndUserDb.get(groupuser);
            users.remove(user);
            List<Message> messages1=new ArrayList<>();
            for(Message message:senderMap.keySet()){
                if(senderMap.get(message).equals(user)){
                    List<Message> messages=groupAndMessageDb.get(groupuser);
                    messages.remove(message);
                    messages1.add(message);
                }
            }
            for(Message message:messages1){
                senderMap.remove(message);
            }

        }
        String mobile=user.getMobile();
        mobileUserDb.remove(mobile);
        int n=0;
        List<User> usersingroup=groupAndUserDb.get(groupuser);
        List<Message> messageList=groupAndMessageDb.get(groupuser);
        n=usersingroup.size()+messageList.size()+senderMap.size();
        return n;
    }

    public String findMessage(Date start, Date end, int k) {

        return "drag";
    }
}
