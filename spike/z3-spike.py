from z3 import *

jessie, evan, john, sarah, matt = Ints('Jessie Evan John Sarah Matt')
developers = [jessie, evan, john, sarah, matt]

s = Solver()
s.add(Distinct(developers))
for developer in developers:
    s.add(developer >= 1, developer <= len(developers))

s.check()

s.add(jessie > 1)
s.add(evan < 5)
s.add(john > 1)
s.add(john < 5)
s.add(sarah < evan)
s.add(matt != john + 1, matt != john - 1)
s.add(john != evan + 1, john != evan - 1)
s.check()

solution = s.model()
print(solution)

def rank(developer):
    return solution.eval(developer).as_long() - 1

developers_in_order = [''] * len(developers)

for developer in developers:
    developers_in_order[rank(developer)] = developer.decl().name()

print(', '.join(developers_in_order))
