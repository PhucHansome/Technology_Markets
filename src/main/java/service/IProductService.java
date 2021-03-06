package service;

import model.Products;

import java.util.List;

public interface IProductService extends IGeneralService<Products> {
    List<Products> findAll();

    Products findById(long productId);

    public List<Products>findNameProducts (String str);

    boolean stopSelling(long productId);

    boolean selling(long productId);

    boolean existByProductId(long userId);

}
