package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    @Transactional
    public void saveItem(Item item) {
        itemRepository.save(item);
    }

    public List<Item> findItems() {
        return itemRepository.findAll();
    }

    public Item findOne(Long itemId) {
        return itemRepository.findOne(itemId);
    }


    // 준영속 엔티티의 수정 방법 (변경 감지 기능)
    @Transactional
    public Item updateItem(Long itemId, Book param) {

        Item findItem = itemRepository.findOne(itemId);  //Id 를 기반으로 실제 DB에 있는 영속성 엔티티를 찾아옴

        findItem.setPrice(param.getPrice());
        findItem.setName(param.getName());
        findItem.setStockQuantity(param.getStockQuantity());

        // findItem으로 찾아온 애는 영속 상태임. 그 후, 값을 세팅하면 스프링의 Transactional 에 의해 커밋됨.
        // 커밋되면 JPA가 영속성 엔티티 중에 변경된 애를 찾음. 그리고 바뀐 값의 업데이트 쿼리를 db 에 날림.

        return findItem;

    }
}
