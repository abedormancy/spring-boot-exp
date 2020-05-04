package ga.vabe.mybatis.controller;

import ga.vabe.mybatis.common.AppContext;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AppController {

    @Autowired
    protected AppContext context;

}
