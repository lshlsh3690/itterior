package com.itterior.itterior.service.impl;

import com.itterior.itterior.domain.ProductImage;
import com.itterior.itterior.dto.ProductPageRequestDTO;
import com.itterior.itterior.domain.page.PageResponseDTO;
import com.itterior.itterior.dto.ProductDTO;
import com.itterior.itterior.dto.ProductPageRequestDTOWithUsername;
import com.itterior.itterior.entity.Product;
import com.itterior.itterior.entity.User;
import com.itterior.itterior.repository.ProductRepository;
import com.itterior.itterior.repository.UserRepository;
import com.itterior.itterior.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    private final UserRepository userRepository;


    @Override
    public PageResponseDTO<ProductDTO> getList(ProductPageRequestDTO pageRequestDTO) {
        Pageable pageable = null;
        if(pageRequestDTO.getOrder() == null || pageRequestDTO.getOrder().equals("desc"))
        {
            pageable = PageRequest.of(
                    pageRequestDTO.getPage() - 1,  //페이지 시작 번호가 0부터 시작하므로
                    pageRequestDTO.getSize(),
                    Sort.by(pageRequestDTO.getSortBy()).descending());
        }else{
            pageable = PageRequest.of(
                    pageRequestDTO.getPage() - 1,  //페이지 시작 번호가 0부터 시작하므로
                    pageRequestDTO.getSize(),
                    Sort.by(pageRequestDTO.getSortBy()).ascending());
        }


        log.info(pageable.toString());

        Page<Object[]> result = productRepository.selectList(pageable, pageRequestDTO.getCategory());

        log.info(result.toString());

        List<ProductDTO> dtoList = result.stream().map(arr -> {
            Product product = (Product) arr[0];
            ProductImage productImage = (ProductImage) arr[1];

            ProductDTO productDTO = ProductDTO.builder()
                    .pno(product.getPno())
                    .pname(product.getPname())
                    .pdesc(product.getPdesc())
                    .price(product.getPrice())
                    .seller(product.getSeller().getUserName())
                    .categoryNum(product.getCategoryNum())
                    .build();
            if (productImage != null){
                String imageStr = productImage.getFileName();
                productDTO.setUploadFileNames(List.of(imageStr));
            }
            return productDTO;
        }).collect(Collectors.toList());

        log.info(dtoList.toString());
        long totalCount = result.getTotalElements();

        return PageResponseDTO.<ProductDTO>withAll()
                .dtoList(dtoList)
                .totalCount(totalCount)
                .pageRequestDTO(pageRequestDTO)
                .build();
    }

    @Override
    public PageResponseDTO<ProductDTO> getListWithUsername(ProductPageRequestDTOWithUsername dto) {
        Pageable pageable = PageRequest.of(
                dto.getPage() - 1,  //페이지 시작 번호가 0부터 시작하므로
                dto.getSize(),
                Sort.by("pno").descending());

        log.info(pageable.toString());

        Page<Object[]> result = productRepository.selectListWithUsername(pageable, dto.getUsername());

        log.info(result.toString());

        List<ProductDTO> dtoList = result.get().map(arr -> {

            Product product = (Product) arr[0];
            ProductImage productImage = (ProductImage) arr[1];


            ProductDTO productDTO = entityToDTO(product);

            if (productImage != null) {
                String imageStr = productImage.getFileName();
                productDTO.setUploadFileNames(List.of(imageStr));
            }

            return productDTO;
        }).collect(Collectors.toList());

        log.info(dtoList.toString());

        long totalCount = result.getTotalElements();


        return PageResponseDTO.<ProductDTO>withAll()
                .dtoList(dtoList)
                .totalCount(totalCount)
                .pageRequestDTO(dto)
                .build();
    }

    @Override
    public ProductDTO get(Long pno) {
        productRepository.incrementProductViewCount(pno);

        Optional<Product> result = productRepository.selectOne(pno);

        Product product = result.orElseThrow();

        ProductDTO productDTO = entityToDTO(product);
        return productDTO;
    }

    private ProductDTO entityToDTO(Product product) {
        ProductDTO productDTO = ProductDTO.builder()
                .pno(product.getPno())
                .pname(product.getPname())
                .pdesc(product.getPdesc())
                .price(product.getPrice())
                .seller(product.getSeller().getUserName())
                .viewCount(product.getViewCount())
                .categoryNum(product.getCategoryNum())
                .build();

        List<ProductImage> imageList = product.getImageList();

        if(imageList == null || imageList.size() == 0 ){
            return productDTO;
        }

        List<String> fileNameList = imageList.stream().map(productImage -> productImage.getFileName()).toList();

        productDTO.setUploadFileNames(fileNameList);

        return productDTO;
    }

    @Override
    public Product register(ProductDTO productDTO) {
        Product product = dtoToEntity(productDTO);


        Product result = productRepository.save(product);

        return result;
    }

    @Override
    public void modify(ProductDTO productDTO) {
        //step1 read
        Optional<Product> result = productRepository.findById(productDTO.getPno());

        Product product = result.orElseThrow();

        //change pname, pdesc, price
        product.changeName(productDTO.getPname());
        product.changeDesc(productDTO.getPdesc());
        product.changePrice(productDTO.getPrice());

        //upload File -- clear first
        product.clearList();

        List<String> uploadFileNames = productDTO.getUploadFileNames();

        if(uploadFileNames != null && uploadFileNames.size() > 0 ){
            uploadFileNames.stream().forEach(uploadName -> {
                product.addImageString(uploadName);
            });
        }
        productRepository.save(product);
    }

    @Override
    public void remove(Long pno) {
        productRepository.updateToDelete(pno, true);
    }

    @Override
    public List<ProductDTO> findTop10ByViewCount() {
        log.info("findTop10BYview");
        Pageable pageable = PageRequest.of(0, 10, Sort.by("viewCount"));
        List<Product> list = productRepository.findTop10ByOrderByViewCountDesc(pageable);

        List<ProductDTO> dtoList = list.stream().map(el -> entityToDTO(el)).collect(Collectors.toList());

        log.info(dtoList.toString());
        return dtoList;
    }

    @Override
    public List<ProductDTO> searchProducts(String query) {
        //검색어를 포함하는 상품 리스트를 가져온다.
        List<Product> products = productRepository.searchWithQuery(query);

        List<ProductDTO> dtoList = products.stream().map(e -> entityToDTO(e)).collect(Collectors.toList());
        return dtoList;
    }

    public PageResponseDTO<ProductDTO> searchProductsInfiniteScroll(String query, Long lastPno){
        Pageable pageable = PageRequest.of(0, 10);
        Page<Object[]> result = productRepository.findByPnoGreaterThanOOrderByPnoAsc(pageable, lastPno);

        log.info(result.toString());

        List<ProductDTO> dtoList = result.get().map(arr -> {

            Product product = (Product) arr[0];
            ProductImage productImage = (ProductImage) arr[1];


            ProductDTO productDTO = entityToDTO(product);

            if (productImage != null) {
                String imageStr = productImage.getFileName();
                productDTO.setUploadFileNames(List.of(imageStr));
            }

            return productDTO;
        }).collect(Collectors.toList());



        return null;
    }

    private Product dtoToEntity(ProductDTO productDTO){

        Product product = Product.builder()
                .pno(productDTO.getPno())
                .pname(productDTO.getPname())
                .pdesc(productDTO.getPdesc())
                .price(productDTO.getPrice())
                .categoryNum(productDTO.getCategoryNum())
                .viewCount(productDTO.getViewCount())
                .build();

        User user = this.userRepository.selectOneByUserName(productDTO.getSeller()).get();

        user.addProduct(product);
        product.setSeller(user);

        //업로드 처리가 끝난 파일들의 이름 리스트
        List<String> uploadFileNames = productDTO.getUploadFileNames();

        if(uploadFileNames == null){
            return product;
        }

        uploadFileNames.stream().forEach(uploadName -> {
            product.addImageString(uploadName);
        });

        return product;
    }
}
