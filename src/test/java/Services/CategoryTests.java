package Services;


import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import lesson6.db.dao.CategoriesMapper;
import lesson6.db.dao.ProductsMapper;
import lesson6.db.model.Categories;
import lesson6.db.model.Products;
import lesson6.db.model.ProductsExample;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import ru.geekbrains.Parts.CategoryType;
import ru.geekbrains.dto.Category;
import ru.geekbrains.dto.ErrorBody;

import ru.geekbrains.service.CategoryService;
import ru.geekbrains.utils.DbUtils;
import ru.geekbrains.utils.RetrofitUtils;


import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class CategoryTests {
    static Retrofit retrofit;
    static CategoryService categoryService;
    static CategoriesMapper categoriesMapper;
    static ProductsMapper productsMapper;

    @BeforeAll
     static void beforeAll() {
        categoriesMapper = DbUtils.getCategoriesMapper();
        productsMapper = DbUtils.getProductsMapper();
        RestAssured.filters(new AllureRestAssured());
        retrofit = RetrofitUtils.getRetrofit();
        categoryService = retrofit.create(CategoryService.class);
    }



    @ParameterizedTest
    @EnumSource( value = CategoryType.class, names = {"FOOD", "FURNITURE", "ELECTRONICS"})
    void getAllCategoryTest(CategoryType type ) throws IOException {
        Response<Category> response = categoryService
                .getCategory(type.getId())
                .execute();
        assertThat(response.isSuccessful()).isTrue();
        assertThat(response.body().getTitle()).isEqualTo(type.getTitle());
        assert response.body() != null;

        Categories categories = categoriesMapper.selectByPrimaryKey(type.getId());

        ProductsExample example = new ProductsExample();
        example.createCriteria().andCategory_idEqualTo(type.getId().longValue());
        List<Products> products = productsMapper.selectByExample(example);
        response.body().getProducts().forEach(e -> {
            assertThat(e.getCategoryTitle()).isEqualTo(categories.getTitle());
        });

    }

    @Test
    void InvalidCategoryTest() throws IOException {
        Response<Category> response = categoryService
                .getCategory(CategoryType.INVALID.getId())
                .execute();
        assertThat(response.isSuccessful()).isFalse();
        assertThat(response.code()).isEqualTo(404);
        if (response != null && !response.isSuccessful() && response.errorBody() != null) {
            ResponseBody body = response.errorBody();
            Converter<ResponseBody, ErrorBody> converter = RetrofitUtils.getRetrofit().responseBodyConverter(ErrorBody.class, new Annotation[0]);
            ErrorBody errorBody = converter.convert(body);
            assertThat(errorBody.getMessage()).isEqualTo("Unable to find category with id: 4");
        }

    }

}
