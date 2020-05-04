package ga.vabe.mybatis.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author abe
 * @since 2020-05-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class TCbmTaskpool implements Serializable {

    private static final long serialVersionUID = 1L;

    private String fPid;

    private String fProvince;

    private String fCity;

    private String fBranch;

    private String fWorkteam;

    private String fOrgRoute;

    private String fTaskname;

    private String fSrctasktoolPid;

    private String fSubstationPid;

    private String fStatusPid;

    private String fYear;

    private String fMonth;

    private String fWeek;

    private String fBelongtoPid;

    private String fEditorPid;

    private String fEditdate;

    private String fEquipmentcontent;

    private String fDescribecontent;

    private String fClassifycontent;

    private String fRemark;

    private String fSysFlag;

    private String fCreator;

    private String fCreateTime;

    private String fLastModifier;

    private String fLastModifiedTime;

    private String fValue1;

    private String fValue2;

    private String fValue3;

    private String fValue4;

    private String fValue5;

    private String fSrcPid;

    private String fItemnameContent;


}
