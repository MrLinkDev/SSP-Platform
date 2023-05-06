package com.ssp.platform.logging;


import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "log")
public class LogEntity {

    @Id
    @Column(name = "date")
    private Timestamp date;

    @Column(name = "username")
    private String username;

    @Column(name = "role")
    private String role;

    @Column(name = "action_controller")
    private String actionController;

    @Column(name = "action_type")
    private String actionType;

    @Column(name = "action_params")
    private String actionParams;

    @Column(name = "action_succeed")
    private boolean actionSucceed;

    @Column(name = "action_error")
    private String actionError;


}
