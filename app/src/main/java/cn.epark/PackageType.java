package cn.epark;

/**
 * created huangzujun on 2019/10/30
 * Describe: 包的类型，分三类：
 *       <p>0：共享车位及公共停车资源都存在
 *       <p>1：公共停车资源
 *       <p>2：共享车位资源
 */
public interface PackageType {

    int EPARK = 0;

    int EP_PUBLIC = 1;

    int EP_SHARE = 2;

}
