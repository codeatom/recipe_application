package recipe_application.application.data.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import recipe_application.application.data.converter.Converter;
import recipe_application.application.data.repo.IngredientRepository;
import recipe_application.application.data.repo.RecipeIngredientRepository;
import recipe_application.application.data.service.IngredientService;
import recipe_application.application.dto.forms.ingredientForm.CreateIngredientForm;
import recipe_application.application.dto.forms.ingredientForm.UpdateIngredientForm;
import recipe_application.application.dto.views.IngredientView;
import recipe_application.application.exception.ResourceNotFoundException;
import recipe_application.application.model.Ingredient;
import recipe_application.application.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Transactional
@Service
public class IngredientServiceImpl implements IngredientService {

    private final IngredientRepository ingredientRepository;
    private final RecipeIngredientRepository recipeIngredientRepository;
    private final Converter converter;

    @Autowired
    public IngredientServiceImpl(IngredientRepository ingredientRepository, RecipeIngredientRepository recipeIngredientRepository, Converter converter) {
        this.ingredientRepository = ingredientRepository;
        this.recipeIngredientRepository = recipeIngredientRepository;
        this.converter = converter;
    }

    @Override
    public IngredientView save(CreateIngredientForm createIngredientForm) {
        if(createIngredientForm == null ){
            throw new IllegalArgumentException ("createIngredientForm is null");
        }

        Ingredient ingredient = ingredientRepository.save(new Ingredient(createIngredientForm.getIngredientName()));

        return converter.ingredientToView(ingredient);
    }

    @Override
    public IngredientView findById(Integer id) {
        if(id < 1 ){
            throw new IllegalArgumentException ("id is 0");
        }

        if(ingredientRepository.findById(id).isPresent()){
            return converter.ingredientToView(ingredientRepository.findById(id).get());
        }

        throw new ResourceNotFoundException("Ingredient with id " + id + " not found.");
    }

    @Override
    public Collection<IngredientView> findAll() {
        Collection<Ingredient> ingredientList = (Collection<Ingredient>) ingredientRepository.findAll();
        return converter.ingredientListToViewList(ingredientList);
    }

    @Override
    public IngredientView findByIngredientNameIgnoreCase(String ingredientName) {
        if(ingredientName == null ){
            throw new IllegalArgumentException ("ingredientName is null");
        }

        if(ingredientRepository.findByIngredientNameIgnoreCase(ingredientName).isPresent()){
            return converter.ingredientToView(ingredientRepository.findByIngredientNameIgnoreCase(ingredientName).get());
        }

        throw new ResourceNotFoundException("Ingredient with name " + ingredientName + " not found.");
    }

    @Override
    public List<IngredientView> findByIngredientNameContainsIgnoreCase(String ingredientName) {
        if(ingredientName == null ){
            throw new IllegalArgumentException ("ingredientName is null");
        }

        Collection<Ingredient> ingredientList = ingredientRepository.findByIngredientNameContainsIgnoreCase(ingredientName);
        Collection<IngredientView> ingredientViews = converter.ingredientListToViewList(ingredientList);

        return new ArrayList<>(ingredientViews);
    }

    @Override
    public IngredientView update(UpdateIngredientForm updateIngredientForm) {
        if(updateIngredientForm == null ){
            throw new IllegalArgumentException ("updateIngredientForm is null");
        }

        Ingredient ingredient = ingredientRepository
                .findById(updateIngredientForm.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Ingredient with id " + updateIngredientForm.getId() + " not found."));

        ingredient.setIngredientName(updateIngredientForm.getIngredientName());

        return converter.ingredientToView(ingredient);
    }

    @Override
    public boolean deleteById(Integer id) {
        if(id < 1 ){
            throw new IllegalArgumentException ("id is 0");
        }

        if(ingredientRepository.existsById(id)){
            removeAssociatedEntity(id);
            ingredientRepository.deleteById(id);
            return true;
        }

        return false;
    }

    @Override
    public boolean delete(Ingredient ingredient) {
        if(ingredient == null ){
            throw new IllegalArgumentException ("ingredient is null");
        }

        if(ingredientRepository.existsById(ingredient.getId())){
            removeAssociatedEntity(ingredient.getId());
            ingredientRepository.delete(ingredient);
            return true;
        }

        return false;
    }

    private void removeAssociatedEntity(Integer id){
        List<RecipeIngredient> recipeIngredientList = recipeIngredientRepository.findAllByIngredientId(id);
        recipeIngredientList.forEach(recipeIngredient -> recipeIngredient.setIngredient(null));
    }

}
