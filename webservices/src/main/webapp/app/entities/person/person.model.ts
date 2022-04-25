export interface IPerson {
  id?: number;
  name?: string | null;
  username?: string | null;
  email?: string | null;
  company?: string | null;
  website?: string | null;
}

export class Person implements IPerson {
  constructor(
    public id?: number,
    public name?: string | null,
    public username?: string | null,
    public email?: string | null,
    public company?: string | null,
    public website?: string | null
  ) {}
}

export function getPersonIdentifier(person: IPerson): number | undefined {
  return person.id;
}
