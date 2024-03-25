import { Entity, PrimaryGeneratedColumn, Column } from 'typeorm';

@Entity()
export class Role {
  @PrimaryGeneratedColumn({ name: 'role_id' })
  roleId: number;

  @Column()
  name: string;
}

