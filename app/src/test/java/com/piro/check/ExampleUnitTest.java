package com.piro.check;

import android.content.Context;
import android.test.AndroidTestCase;
import android.util.Log;

import com.piro.check.persistance.DBHelper;
import com.piro.check.persistance.User;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(MockitoJUnitRunner.class)
public class ExampleUnitTest {

    @Mock
    Context mMockContext;

    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testCRUD() throws Exception{
        DBHelper db= new DBHelper(mMockContext);

        System.out.println("start insert");

        db.addUser(new User(1,"pesho",0d));
        db.addUser(new User(2,"gosho",-1d));
        db.addUser(new User(1,"misho",10.4d));

        System.out.println("get All");
        List<User> users = db.getAllUsers();
        for(User user : users){
            System.out.println(user.getName());
        }
    }
}