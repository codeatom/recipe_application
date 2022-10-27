package recipe_application.application.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import recipe_application.application.data.service.RecipeInstructionService;
import recipe_application.application.data.service.RecipeService;
import recipe_application.application.dto.forms.recipeForm.CreateRecipeForm;
import recipe_application.application.dto.forms.recipeForm.UpdateRecipeForm;
import recipe_application.application.dto.forms.recipeInstructionForm.CreateRecipeInstructionForm;
import recipe_application.application.dto.forms.recipeInstructionForm.UpdateRecipeInstructionForm;
import recipe_application.application.dto.views.RecipeInstructionView;
import recipe_application.application.dto.views.RecipeView;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;


@Controller
@RequestMapping("/api/v1/recipe")
public class RecipeController {

    private final RecipeService recipeService;
    private final RecipeInstructionService recipeInstructionService;

    @Autowired
    public RecipeController(RecipeService recipeService, RecipeInstructionService recipeInstructionService) {
        this.recipeService = recipeService;
        this.recipeInstructionService = recipeInstructionService;
    }

    @GetMapping("/list")
    public ResponseEntity<Collection<RecipeView>> getRecipeList() {
        return ResponseEntity.ok(recipeService.findAll());
    }

    @GetMapping("/find-by-id/{id}")
    public ResponseEntity<RecipeView> getRecipeById(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(recipeService.findById(id));
    }

    @GetMapping("/find-by-name")
    public ResponseEntity<List<RecipeView>> getRecipeByRecipeName(@RequestParam("recipeName") String recipeName) {
        return ResponseEntity.ok(recipeService.findAllByRecipeNameContainingIgnoreCase(recipeName));
    }

    @PostMapping("/add")
    public ResponseEntity<RecipeView> addRecipe(@Valid @RequestBody CreateRecipeForm createRecipeForm) {
        CreateRecipeInstructionForm createRecipeInstructionForm = new CreateRecipeInstructionForm();
        createRecipeInstructionForm.setTitle(createRecipeForm.getInstructionTitle());
        createRecipeInstructionForm.setInstruction(createRecipeForm.getInstructionDetail());

        RecipeInstructionView recipeInstructionView = recipeInstructionService.save(createRecipeInstructionForm);

        createRecipeForm.setInstructionId(recipeInstructionView.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(recipeService.save(createRecipeForm));

    }

    @PutMapping("/update/{id}")
    public ResponseEntity<RecipeView> updateRecipe(@PathVariable("id") Integer id, @Valid @RequestBody UpdateRecipeForm updateRecipeForm) {
        RecipeView recipeView = recipeService.findById(id);

        UpdateRecipeInstructionForm updateRecipeInstructionForm = new UpdateRecipeInstructionForm();

        updateRecipeInstructionForm.setId(recipeView.getInstruction().getId());
        updateRecipeInstructionForm.setTitle(updateRecipeForm.getInstructionTitle());
        updateRecipeInstructionForm.setInstruction(updateRecipeForm.getInstructionDetail());
        recipeInstructionService.update(updateRecipeInstructionForm);

        updateRecipeForm.setInstructionId(recipeView.getInstruction().getId());
        updateRecipeForm.setId(id);

        return ResponseEntity.status(HttpStatus.CREATED).body(recipeService.update(updateRecipeForm));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable("id") Integer id) {
        recipeService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}